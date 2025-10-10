package com.example.fraud.consumer.service;

import com.example.fraud.common.dto.TransactionEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class FraudDetectionService {

    private static final double HIGH_AMOUNT_THRESHOLD = 5000.0;
    private static final double LOCATION_CHANGE_WEIGHT = 0.25;
    private static final double HIGH_AMOUNT_WEIGHT = 0.6;
    private static final double FAST_REUSE_WEIGHT = 0.2;
    private static final Duration RAPID_USAGE_WINDOW = Duration.ofMinutes(10);

    public FraudEvaluation evaluate(TransactionEvent event, AccountAggregate aggregate) {
        double score = 0;
        List<String> reasons = new ArrayList<>();

        if (event.getAmount() >= HIGH_AMOUNT_THRESHOLD) {
            score += HIGH_AMOUNT_WEIGHT;
            reasons.add("Amount exceeds $" + (int) HIGH_AMOUNT_THRESHOLD);
        }

        if (aggregate != null && aggregate.getAverageAmount() > 0 &&
                event.getAmount() > aggregate.getAverageAmount() * 3) {
            score += 0.25;
            reasons.add("Amount is 3x above average spend for account");
        }

        if (aggregate != null && StringUtils.hasText(aggregate.getLastLocation()) &&
                event.getLocation() != null &&
                !aggregate.getLastLocation().equalsIgnoreCase(event.getLocation())) {
            score += LOCATION_CHANGE_WEIGHT;
            reasons.add("Location changed from " + aggregate.getLastLocation());
        }

        if (aggregate != null && aggregate.getLastTimestamp() != null) {
            Instant now = event.getTimestamp() != null ? event.getTimestamp() : Instant.now();
            Duration sinceLast = Duration.between(aggregate.getLastTimestamp(), now);
            if (!sinceLast.isNegative() && sinceLast.compareTo(RAPID_USAGE_WINDOW) <= 0) {
                score += FAST_REUSE_WEIGHT;
                reasons.add("Multiple transactions within " + sinceLast.toMinutes() + " minutes");
            }
        }

        double normalizedScore = Math.min(score, 1.0);
        boolean fraudulent = normalizedScore >= 0.7;
        String reason = reasons.isEmpty() ? "No suspicious signals detected" : String.join("; ", reasons);

        return new FraudEvaluation(normalizedScore, fraudulent, reason);
    }

    public static class FraudEvaluation {
        private final double score;
        private final boolean fraudulent;
        private final String reason;

        public FraudEvaluation(double score, boolean fraudulent, String reason) {
            this.score = score;
            this.fraudulent = fraudulent;
            this.reason = reason;
        }

        public double getScore() {
            return score;
        }

        public boolean isFraudulent() {
            return fraudulent;
        }

        public String getReason() {
            return reason;
        }
    }
}
