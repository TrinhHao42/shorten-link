package iuh.fit.se.shortenlink.controller;

import iuh.fit.se.shortenlink.dto.common.ApiResponse;
import iuh.fit.se.shortenlink.dto.file.FileUploadResponse;
import iuh.fit.se.shortenlink.service.CurrentUserService;
import iuh.fit.se.shortenlink.service.FileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;
    private final CurrentUserService currentUserService;

    public FileController(FileService fileService, CurrentUserService currentUserService) {
        this.fileService = fileService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/upload")
    public ApiResponse<FileUploadResponse> upload(@RequestParam("file") MultipartFile file,
                                                  @RequestParam(value = "password", required = false) String password) {
        Long userId = currentUserService.currentUserId();
        return ApiResponse.ok(fileService.upload(userId, file, password));
    }

    @GetMapping("/download/{slug}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String slug,
                                                        @RequestParam(value = "password", required = false) String password) {
        FileService.FileDownloadContent content = fileService.resolveDownloadContent(slug, password);
        MediaType mediaType = content.contentType() == null || content.contentType().isBlank()
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(content.contentType());

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(content.fileName() == null || content.fileName().isBlank() ? slug : content.fileName())
                .build();

        ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(mediaType);

        if (content.contentLength() > 0) {
            builder.contentLength(content.contentLength());
        }

        return builder.body(new InputStreamResource(content.inputStream()));
    }
}
