package com.locus.maps.directions.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("locus.google.maps")
@Data
public class ApiConfiguration {
    private String apiKey;
    private String apiUrl;
}
