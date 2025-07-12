package iuh.fit.se.shortenlink.controller;

import iuh.fit.se.shortenlink.dto.common.ApiResponse;
import iuh.fit.se.shortenlink.dto.notification.ShareLinkRequest;
import iuh.fit.se.shortenlink.service.CurrentUserService;
import iuh.fit.se.shortenlink.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/share-link")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public NotificationController(NotificationService notificationService, CurrentUserService currentUserService) {
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ApiResponse<Void> share(@Valid @RequestBody ShareLinkRequest request) {
        notificationService.shareLink(currentUserService.currentUserId(), request);
        return ApiResponse.ok(null);
    }
}

