package com.example.demo.youtube;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "youtube")
@Getter
@Setter
public class YoutubeProperties {

    private String credentials;

    private String applicationName;

    private String redirectUri;

}
