package iuh.fit.se.shortenlink.service;

import iuh.fit.se.shortenlink.exception.AppException;
import iuh.fit.se.shortenlink.exception.ErrorCode;
import iuh.fit.se.shortenlink.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser principal)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return principal.id();
    }
}

