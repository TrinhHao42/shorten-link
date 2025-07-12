package iuh.fit.se.shortenlink.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class SlugService {

    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 8;
    private final SecureRandom random = new SecureRandom();

    public String generateSlug() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    public boolean isAlphanumeric(String slug) {
        return slug != null && slug.matches("^[a-zA-Z0-9]+$");
    }
}

