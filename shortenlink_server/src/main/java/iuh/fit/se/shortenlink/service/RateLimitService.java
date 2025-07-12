package iuh.fit.se.shortenlink.service;

import iuh.fit.se.shortenlink.exception.AppException;
import iuh.fit.se.shortenlink.exception.ErrorCode;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<Long, Bucket> bucketMap = new ConcurrentHashMap<>();

    public void validateCreateLinkLimit(Long userId) {
        Bucket bucket = bucketMap.computeIfAbsent(userId, this::newBucket);
        if (!bucket.tryConsume(1)) {
            throw new AppException(ErrorCode.RATE_LIMIT_EXCEEDED, "Rate limit exceeded for link creation");
        }
    }

    private Bucket newBucket(Long ignored) {
        Bandwidth bandwidth = Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(bandwidth).build();
    }
}
