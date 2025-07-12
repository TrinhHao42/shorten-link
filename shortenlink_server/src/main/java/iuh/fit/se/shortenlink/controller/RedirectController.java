package iuh.fit.se.shortenlink.controller;

import iuh.fit.se.shortenlink.exception.AppException;
import iuh.fit.se.shortenlink.exception.ErrorCode;
import iuh.fit.se.shortenlink.service.LinkService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {

    private final LinkService linkService;

    public RedirectController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/{slug:[a-zA-Z0-9]+}")
    public ResponseEntity<Void> redirect(@PathVariable String slug) {
        String originalUrl = linkService.resolveOriginalUrl(slug)
                .orElseThrow(() -> new AppException(ErrorCode.LINK_NOT_FOUND));

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}

