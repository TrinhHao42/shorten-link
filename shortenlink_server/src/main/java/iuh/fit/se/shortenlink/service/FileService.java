package iuh.fit.se.shortenlink.service;

import iuh.fit.se.shortenlink.dto.file.FileUploadResponse;
import iuh.fit.se.shortenlink.dto.link.CreateLinkRequest;
import iuh.fit.se.shortenlink.entity.FileObject;
import iuh.fit.se.shortenlink.entity.Link;
import iuh.fit.se.shortenlink.entity.enums.LinkType;
import iuh.fit.se.shortenlink.exception.AppException;
import iuh.fit.se.shortenlink.exception.ErrorCode;
import iuh.fit.se.shortenlink.integration.s3.S3StorageService;
import iuh.fit.se.shortenlink.repository.FileObjectRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Service
public class FileService {

    private final LinkService linkService;
    private final FileObjectRepository fileObjectRepository;
    private final S3StorageService s3StorageService;
    private final PasswordEncoder passwordEncoder;

    public FileService(LinkService linkService,
                       FileObjectRepository fileObjectRepository,
                       S3StorageService s3StorageService,
                       PasswordEncoder passwordEncoder) {
        this.linkService = linkService;
        this.fileObjectRepository = fileObjectRepository;
        this.s3StorageService = s3StorageService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public FileUploadResponse upload(Long userId, MultipartFile file, String password) {
        String s3Key = s3StorageService.upload(file);

        CreateLinkRequest createLinkRequest = new CreateLinkRequest();
        createLinkRequest.setOriginalUrl("s3://" + s3Key);
        createLinkRequest.setPassword(password);

        var linkResponse = linkService.create(userId, createLinkRequest, LinkType.FILE);
        Link link = linkService.findBySlug(linkResponse.slug());

        FileObject fileObject = FileObject.builder()
                .link(link)
                .s3Key(s3Key)
                .fileName(file.getOriginalFilename() == null ? s3Key : file.getOriginalFilename())
                .fileSize(file.getSize())
                .build();

        fileObjectRepository.save(fileObject);

        return FileUploadResponse.builder()
                .slug(linkResponse.slug())
                .downloadUrl("/files/download/" + linkResponse.slug())
                .build();
    }

    public URL resolveDownloadUrl(String slug, String password) {
        FileObject fileObject = resolveFileObjectForDownload(slug, password);
        return s3StorageService.generatePresignedDownloadUrl(fileObject.getS3Key());
    }

    public FileDownloadContent resolveDownloadContent(String slug, String password) {
        FileObject fileObject = resolveFileObjectForDownload(slug, password);
        URL url = s3StorageService.generatePresignedDownloadUrl(fileObject.getS3Key());
        try {
            URLConnection connection = url.openConnection();
            return new FileDownloadContent(
                    connection.getInputStream(),
                    fileObject.getFileName(),
                    connection.getContentType(),
                    connection.getContentLengthLong()
            );
        } catch (IOException ex) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND, "Failed to open file stream");
        }
    }

    private FileObject resolveFileObjectForDownload(String slug, String password) {
        Link link = linkService.findBySlug(slug);
        if (link.getType() != LinkType.FILE) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }

        if (link.getPasswordHash() != null) {
            if (password == null || !passwordEncoder.matches(password, link.getPasswordHash())) {
                throw new AppException(ErrorCode.FORBIDDEN, "File link requires valid password");
            }
        }

        return fileObjectRepository.findByLink(link)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));
    }

    public record FileDownloadContent(InputStream inputStream,
                                      String fileName,
                                      String contentType,
                                      long contentLength) {
    }

    @Transactional
    public void deleteFileLink(Link link) {
        fileObjectRepository.findByLink(link).ifPresent(fileObject -> {
            s3StorageService.delete(fileObject.getS3Key());
            fileObjectRepository.delete(fileObject);
        });
    }
}
