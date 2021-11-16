package com.locus.maps.directions.config;

import com.google.maps.GeoApiContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

@Configuration
public class GeoApiContextConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ApiConfiguration apiConfiguration;

    public GeoApiContextConfiguration(ApiConfiguration apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
    }

    @Bean
    public GeoApiContext geoApiContext() {
        logger.info("Setting up Geolocation Api Context");
        return new GeoApiContext.Builder()
                .apiKey(apiConfiguration.getApiKey())
                .queryRateLimit(3)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }
}
