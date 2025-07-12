package iuh.fit.se.shortenlink.integration.s3;

import iuh.fit.se.shortenlink.config.S3Properties;
import iuh.fit.se.shortenlink.exception.AppException;
import iuh.fit.se.shortenlink.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3StorageService {

    private static final Logger log = LoggerFactory.getLogger(S3StorageService.class);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    public S3StorageService(S3Client s3Client, S3Presigner s3Presigner, S3Properties s3Properties) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.s3Properties = s3Properties;
    }

    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "File is required");
        }

        String bucket = s3Properties.getBucket();
        if (bucket == null || bucket.isBlank()) {
            throw new AppException(ErrorCode.INTERNAL_ERROR, "S3 bucket is not configured");
        }

        String originalName = file.getOriginalFilename();
        String safeName = (originalName == null || originalName.isBlank()) ? "file.bin" : originalName;
        String key = UUID.randomUUID() + "-" + safeName;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
            return key;
        } catch (S3Exception ex) {
            // AWS returned an S3 service error (permissions, bucket/region mismatch, etc.)
            log.error("S3 upload failed. bucket={}, key={}, status={}, code={}, requestId={}, message={}",
                    bucket,
                    key,
                    ex.statusCode(),
                    ex.awsErrorDetails() != null ? ex.awsErrorDetails().errorCode() : "unknown",
                    ex.requestId(),
                    ex.awsErrorDetails() != null ? ex.awsErrorDetails().errorMessage() : ex.getMessage(),
                    ex);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Failed to upload file to S3");
        } catch (SdkClientException ex) {
            // Client-side AWS SDK issue (credentials/network/DNS/proxy)
            log.error("S3 client error during upload. bucket={}, key={}, region={}, message={}",
                    bucket,
                    key,
                    s3Properties.getRegion(),
                    ex.getMessage(),
                    ex);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Failed to upload file to S3");
        } catch (Exception ex) {
            log.error("Unexpected upload error. bucket={}, key={}, message={}", bucket, key, ex.getMessage(), ex);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Failed to upload file to S3");
        }
    }

    public URL generatePresignedDownloadUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofSeconds(s3Properties.getPresignedTtlSeconds()))
                .build();

        return s3Presigner.presignGetObject(presignRequest).url();
    }

    public void delete(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(key)
                .build();
        s3Client.deleteObject(deleteRequest);
    }
}
