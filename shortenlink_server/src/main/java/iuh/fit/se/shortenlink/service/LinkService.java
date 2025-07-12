package iuh.fit.se.shortenlink.service;

import iuh.fit.se.shortenlink.dto.link.CreateLinkRequest;
import iuh.fit.se.shortenlink.dto.link.LinkResponse;
import iuh.fit.se.shortenlink.dto.link.UpdateLinkRequest;
import iuh.fit.se.shortenlink.entity.Link;
import iuh.fit.se.shortenlink.entity.User;
import iuh.fit.se.shortenlink.entity.enums.LinkType;
import iuh.fit.se.shortenlink.exception.AppException;
import iuh.fit.se.shortenlink.exception.ErrorCode;
import iuh.fit.se.shortenlink.integration.s3.S3StorageService;
import iuh.fit.se.shortenlink.repository.FileObjectRepository;
import iuh.fit.se.shortenlink.repository.LinkRepository;
import iuh.fit.se.shortenlink.repository.UserRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LinkService {

    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final FileObjectRepository fileObjectRepository;
    private final S3StorageService s3StorageService;
    private final SlugService slugService;
    private final SlugBloomFilterService bloomFilterService;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final RateLimitService rateLimitService;

    public LinkService(LinkRepository linkRepository,
                       UserRepository userRepository,
                       FileObjectRepository fileObjectRepository,
                       S3StorageService s3StorageService,
                       SlugService slugService,
                       SlugBloomFilterService bloomFilterService,
                       StringRedisTemplate redisTemplate,
                       PasswordEncoder passwordEncoder,
                       RateLimitService rateLimitService) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
        this.fileObjectRepository = fileObjectRepository;
        this.s3StorageService = s3StorageService;
        this.slugService = slugService;
        this.bloomFilterService = bloomFilterService;
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
        this.rateLimitService = rateLimitService;
    }

    @Transactional
    public LinkResponse create(Long userId, CreateLinkRequest request, LinkType type) {
        rateLimitService.validateCreateLinkLimit(userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        String slug = resolveSlug(request.getCustomSlug());

        Link link = Link.builder()
                .user(user)
                .originalUrl(request.getOriginalUrl())
                .slug(slug)
                .type(type)
                .passwordHash(optionalHash(request.getPassword()))
                .build();

        Link persisted = linkRepository.save(link);
        bloomFilterService.put(persisted.getSlug());
        return mapToResponse(persisted);
    }

    public List<LinkResponse> list(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        return linkRepository.findByUser(user).stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public LinkResponse update(Long userId, Long linkId, UpdateLinkRequest request) {
        Link link = findOwnedLink(userId, linkId);

        if (request.getOriginalUrl() != null && !request.getOriginalUrl().isBlank()) {
            link.setOriginalUrl(request.getOriginalUrl());
        }

        if (request.getSlug() != null && !request.getSlug().isBlank() && !request.getSlug().equals(link.getSlug())) {
            if (!slugService.isAlphanumeric(request.getSlug())) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Slug must be alphanumeric");
            }
            if (slugExists(request.getSlug())) {
                throw new AppException(ErrorCode.SLUG_COLLISION);
            }
            redisTemplate.delete(cacheKey(link.getSlug()));
            link.setSlug(request.getSlug());
            bloomFilterService.put(request.getSlug());
        }

        return mapToResponse(linkRepository.save(link));
    }

    @Transactional
    public void delete(Long userId, Long linkId) {
        Link link = findOwnedLink(userId, linkId);

        if (link.getType() == LinkType.FILE) {
            fileObjectRepository.findByLink(link).ifPresent(file -> {
                s3StorageService.delete(file.getS3Key());
                fileObjectRepository.delete(file);
            });
        }

        linkRepository.delete(link);
        redisTemplate.delete(cacheKey(link.getSlug()));
    }

    public Optional<String> resolveOriginalUrl(String slug) {
        String cached = redisTemplate.opsForValue().get(cacheKey(slug));
        if (cached != null) {
            return Optional.of(cached);
        }

        return linkRepository.findBySlug(slug).map(link -> {
            redisTemplate.opsForValue().set(cacheKey(slug), link.getOriginalUrl());
            link.setClickCount(link.getClickCount() + 1);
            linkRepository.save(link);
            return link.getOriginalUrl();
        });
    }

    public Link findBySlug(String slug) {
        return linkRepository.findBySlug(slug).orElseThrow(() -> new AppException(ErrorCode.LINK_NOT_FOUND));
    }

    private Link findOwnedLink(Long userId, Long linkId) {
        Link link = linkRepository.findById(linkId).orElseThrow(() -> new AppException(ErrorCode.LINK_NOT_FOUND));
        if (!link.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        return link;
    }

    private String resolveSlug(String customSlug) {
        if (customSlug != null && !customSlug.isBlank()) {
            if (!slugService.isAlphanumeric(customSlug)) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Slug must be alphanumeric");
            }
            if (slugExists(customSlug)) {
                throw new AppException(ErrorCode.SLUG_COLLISION);
            }
            return customSlug;
        }

        for (int attempt = 0; attempt < 10; attempt++) {
            String generated = slugService.generateSlug();
            if (!slugExists(generated)) {
                return generated;
            }
        }
        throw new AppException(ErrorCode.SLUG_COLLISION, "Failed to generate unique slug");
    }

    private boolean slugExists(String slug) {
        return bloomFilterService.mightContain(slug) && linkRepository.existsBySlug(slug);
    }

    private String optionalHash(String password) {
        if (password == null || password.isBlank()) {
            return null;
        }
        return passwordEncoder.encode(password);
    }

    private LinkResponse mapToResponse(Link link) {
        return LinkResponse.builder()
                .id(link.getId())
                .originalUrl(link.getOriginalUrl())
                .slug(link.getSlug())
                .type(link.getType())
                .clickCount(link.getClickCount())
                .build();
    }

    private String cacheKey(String slug) {
        return "link:" + slug;
    }
}

