package iuh.fit.se.shortenlink.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

@Configuration
public class S3StartupValidation {

    private static final Logger log = LoggerFactory.getLogger(S3StartupValidation.class);

    @Bean
    public CommandLineRunner validateS3Config(S3Client s3Client, S3Properties s3Properties) {
        return args -> {
            String bucket = s3Properties.getBucket();
            String region = s3Properties.getRegion();

            if (bucket == null || bucket.isBlank()) {
                log.warn("S3 bucket is not configured. File upload will fail. Set app.aws.s3.bucket property.");
                return;
            }

            try {
                HeadBucketRequest headRequest = HeadBucketRequest.builder().bucket(bucket).build();
                s3Client.headBucket(headRequest);
                log.info("S3 bucket validation SUCCESS: bucket={}, region={}", bucket, region);
            } catch (Exception ex) {
                log.error("S3 bucket validation FAILED: bucket={}, region={}, error={}",
                        bucket, region, ex.getMessage());
                log.error("Verify: 1) Bucket name correct, 2) AWS credentials set, 3) Region matches bucket location");
                throw new RuntimeException("S3 bucket configuration is invalid. File upload will not work.", ex);
            }
        };
    }
}

