package com.example.fraud.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    @JsonProperty("transaction_id")
    private String transactionId = UUID.randomUUID().toString();

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("location")
    private String location;

    @JsonProperty("timestamp")
    private Instant timestamp = Instant.now();

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("currency")
    private String currency = "USD";

}
