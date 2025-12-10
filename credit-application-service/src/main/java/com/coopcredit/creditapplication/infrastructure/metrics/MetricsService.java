package com.coopcredit.creditapplication.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

/**
 * Service for managing custom application metrics.
 * Tracks endpoint times, errors, and authentication failures.
 */
@Service
public class MetricsService {
    
    private final MeterRegistry meterRegistry;
    
    // Counters
    private final Counter authenticationFailures;
    private final Counter businessErrors;
    private final Counter validationErrors;
    private final Counter applicationsCreated;
    private final Counter applicationsApproved;
    private final Counter applicationsRejected;
    
    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Initialize counters
        this.authenticationFailures = Counter.builder("authentication.failures")
            .description("Number of failed authentication attempts")
            .tag("type", "security")
            .register(meterRegistry);
        
        this.businessErrors = Counter.builder("business.errors")
            .description("Number of business rule violations")
            .tag("type", "error")
            .register(meterRegistry);
        
        this.validationErrors = Counter.builder("validation.errors")
            .description("Number of validation errors")
            .tag("type", "error")
            .register(meterRegistry);
        
        this.applicationsCreated = Counter.builder("credit.applications.created")
            .description("Total credit applications created")
            .tag("type", "business")
            .register(meterRegistry);
        
        this.applicationsApproved = Counter.builder("credit.applications.approved")
            .description("Total credit applications approved")
            .tag("type", "business")
            .register(meterRegistry);
        
        this.applicationsRejected = Counter.builder("credit.applications.rejected")
            .description("Total credit applications rejected")
            .tag("type", "business")
            .register(meterRegistry);
    }
    
    /**
     * Records an authentication failure.
     */
    public void incrementAuthenticationFailure() {
        authenticationFailures.increment();
    }
    
    /**
     * Records a business error.
     */
    public void incrementBusinessError() {
        businessErrors.increment();
    }
    
    /**
     * Records a validation error.
     */
    public void incrementValidationError() {
        validationErrors.increment();
    }
    
    /**
     * Records a credit application creation.
     */
    public void incrementApplicationCreated() {
        applicationsCreated.increment();
    }
    
    /**
     * Records a credit application approval.
     */
    public void incrementApplicationApproved() {
        applicationsApproved.increment();
    }
    
    /**
     * Records a credit application rejection.
     */
    public void incrementApplicationRejected() {
        applicationsRejected.increment();
    }
    
    /**
     * Creates a timer for endpoint execution.
     *
     * @param endpoint the endpoint name
     * @return timer instance
     */
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }
    
    /**
     * Records endpoint execution time.
     *
     * @param sample the timer sample
     * @param endpoint the endpoint name
     * @param status the response status
     */
    public void recordEndpointTime(Timer.Sample sample, String endpoint, String status) {
        sample.stop(Timer.builder("endpoint.execution.time")
            .description("Time taken to execute endpoint")
            .tag("endpoint", endpoint)
            .tag("status", status)
            .register(meterRegistry));
    }
}
