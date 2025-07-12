package iuh.fit.se.shortenlink.service;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import iuh.fit.se.shortenlink.repository.LinkRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class SlugBloomFilterService {

    private final LinkRepository linkRepository;
    private BloomFilter<CharSequence> bloomFilter;

    public SlugBloomFilterService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @PostConstruct
    void init() {
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), 1_000_000, 0.01);
        linkRepository.findAll().forEach(link -> bloomFilter.put(link.getSlug()));
    }

    public boolean mightContain(String slug) {
        return bloomFilter.mightContain(slug);
    }

    public void put(String slug) {
        bloomFilter.put(slug);
    }
}

