package com.coopcredit.riskcentral;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Risk Central Service.
 * Mock external service for credit risk evaluation.
 */
@SpringBootApplication
public class RiskCentralApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RiskCentralApplication.class, args);
    }
}
