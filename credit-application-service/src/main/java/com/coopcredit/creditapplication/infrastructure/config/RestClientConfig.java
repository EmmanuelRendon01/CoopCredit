package com.coopcredit.creditapplication.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for REST client beans.
 */
@Configuration
public class RestClientConfig {
    
    /**
     * Creates RestTemplate bean for HTTP calls.
     *
     * @return configured RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
