package com.internet.banking.orchestrator.microservice.policy;
import java.time.Duration;

public class SagaRetryPolicy {

    private final int maxAttempts;
    private final Duration initialDelay;
    private final double backoffMultiplier;

    public SagaRetryPolicy(int maxAttempts, Duration initialDelay, double backoffMultiplier) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be greater than zero");
        }

        if (initialDelay == null || initialDelay.isNegative() || initialDelay.isZero()) {
            throw new IllegalArgumentException("initialDelay must be greater than zero");
        }

        if (backoffMultiplier < 1.0) {
            throw new IllegalArgumentException("backoffMultiplier must be greater than or equal to 1.0");
        }

        this.maxAttempts = maxAttempts;
        this.initialDelay = initialDelay;
        this.backoffMultiplier = backoffMultiplier;
    }

    public static SagaRetryPolicy defaultPolicy() {
        return new SagaRetryPolicy(
                3,
                Duration.ofSeconds(5),
                2.0
        );
    }

    public boolean canRetry(int currentAttempt) {
        return currentAttempt < maxAttempts;
    }

    public Duration calculateNextDelay(int currentAttempt) {
        if (currentAttempt <= 1) {
            return initialDelay;
        }

        double multiplier = Math.pow(backoffMultiplier, currentAttempt - 1);
        long delayMillis = Math.round(initialDelay.toMillis() * multiplier);

        return Duration.ofMillis(delayMillis);
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public Duration getInitialDelay() {
        return initialDelay;
    }

    public double getBackoffMultiplier() {
        return backoffMultiplier;
    }
}
