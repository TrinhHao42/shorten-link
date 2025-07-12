package iuh.fit.se.shortenlink.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.aws.s3")
public class S3Properties {
    private String region = "ap-southeast-1";
    private String bucket;
    private Long presignedTtlSeconds = 900L;
}

