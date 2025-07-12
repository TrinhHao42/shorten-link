package iuh.fit.se.shortenlink.controller;

import iuh.fit.se.shortenlink.service.FileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
public class PublicFileController {

    private final FileService fileService;

    public PublicFileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{slug}")
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
