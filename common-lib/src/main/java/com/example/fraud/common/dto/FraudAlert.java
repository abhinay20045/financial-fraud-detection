package com.example.fraud.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Represents a fraud alert message.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlert {

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("score")
    private double score;

    @JsonProperty("is_fraud")
    private boolean isFraud;

    @JsonProperty("detected_at")
    private Instant detectedAt = Instant.now();

    @JsonProperty("reason")
    private String reason;
}
