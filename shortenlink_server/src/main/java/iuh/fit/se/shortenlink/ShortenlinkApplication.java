package iuh.fit.se.shortenlink;

import iuh.fit.se.shortenlink.config.JwtProperties;
import iuh.fit.se.shortenlink.config.S3Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, S3Properties.class})
public class ShortenlinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortenlinkApplication.class, args);
    }
}
