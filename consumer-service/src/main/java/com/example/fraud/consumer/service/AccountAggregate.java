package com.example.fraud.consumer.service;

import java.time.Instant;

/**
 * Snapshot of recent aggregate metrics for an account. Backed by Redis but can be
 * instantiated from other data sources for testing.
 */
public class AccountAggregate {

    private final String accountId;
    private final long transactionCount;
    private final double totalAmount;
    private final double averageAmount;
    private final double lastAmount;
    private final Instant lastTimestamp;
    private final String lastLocation;

    public AccountAggregate(String accountId,
                            long transactionCount,
                            double totalAmount,
                            double averageAmount,
                            double lastAmount,
                            Instant lastTimestamp,
                            String lastLocation) {
        this.accountId = accountId;
        this.transactionCount = transactionCount;
        this.totalAmount = totalAmount;
        this.averageAmount = averageAmount;
        this.lastAmount = lastAmount;
        this.lastTimestamp = lastTimestamp;
        this.lastLocation = lastLocation;
    }

    public String getAccountId() {
        return accountId;
    }

    public long getTransactionCount() {
        return transactionCount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getAverageAmount() {
        return averageAmount;
    }

    public double getLastAmount() {
        return lastAmount;
    }

    public Instant getLastTimestamp() {
        return lastTimestamp;
    }

    public String getLastLocation() {
        return lastLocation;
    }
}
