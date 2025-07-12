package iuh.fit.se.shortenlink.controller;

import iuh.fit.se.shortenlink.dto.common.ApiResponse;
import iuh.fit.se.shortenlink.dto.link.CreateLinkRequest;
import iuh.fit.se.shortenlink.dto.link.LinkResponse;
import iuh.fit.se.shortenlink.dto.link.UpdateLinkRequest;
import iuh.fit.se.shortenlink.entity.enums.LinkType;
import iuh.fit.se.shortenlink.service.CurrentUserService;
import iuh.fit.se.shortenlink.service.LinkService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/links")
public class LinkController {

    private final LinkService linkService;
    private final CurrentUserService currentUserService;

    public LinkController(LinkService linkService, CurrentUserService currentUserService) {
        this.linkService = linkService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ApiResponse<LinkResponse> createLink(@Valid @RequestBody CreateLinkRequest request) {
        Long userId = currentUserService.currentUserId();
        return ApiResponse.ok(linkService.create(userId, request, LinkType.URL));
    }

    @GetMapping
    public ApiResponse<List<LinkResponse>> listLinks() {
        Long userId = currentUserService.currentUserId();
        return ApiResponse.ok(linkService.list(userId));
    }

    @PutMapping("/{id}")
    public ApiResponse<LinkResponse> updateLink(@PathVariable Long id, @RequestBody UpdateLinkRequest request) {
        Long userId = currentUserService.currentUserId();
        return ApiResponse.ok(linkService.update(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteLink(@PathVariable Long id) {
        Long userId = currentUserService.currentUserId();
        linkService.delete(userId, id);
        return ApiResponse.ok(null);
    }
}
