package iuh.fit.se.shortenlink.service;

import iuh.fit.se.shortenlink.config.JwtProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RefreshTokenStore {

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtProperties jwtProperties;

    public RefreshTokenStore(StringRedisTemplate stringRedisTemplate, JwtProperties jwtProperties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jwtProperties = jwtProperties;
    }

    public void save(Long userId, String token) {
        stringRedisTemplate.opsForValue()
                .set(redisKey(userId), token, Duration.ofSeconds(jwtProperties.getRefreshTokenTtlSeconds()));
    }

    public Optional<String> get(Long userId) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(redisKey(userId)));
    }

    private String redisKey(Long userId) {
        return "refresh:user:" + userId;
    }
}

