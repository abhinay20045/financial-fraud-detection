package com.example.fraud.consumer.metrics;

import com.example.fraud.common.dto.TransactionEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class FraudMetrics {

    private final Counter transactionsCounter;
    private final Counter fraudulentTransactionsCounter;
    private final DistributionSummary transactionAmountSummary;
    private final Timer processingTimer;

    // Accept a nullable MeterRegistry so the app can start even if micrometer
    // isn't configured in the environment. When null, metrics become no-ops.
    public FraudMetrics(@Nullable MeterRegistry meterRegistry) {
        if (meterRegistry != null) {
            this.transactionsCounter = Counter.builder("fraud.transactions.processed")
                    .description("Number of processed transactions")
                    .register(meterRegistry);

            this.fraudulentTransactionsCounter = Counter.builder("fraud.transactions.flagged")
                    .description("Number of transactions flagged as fraudulent")
                    .register(meterRegistry);

            this.transactionAmountSummary = DistributionSummary.builder("fraud.transactions.amount")
                    .description("Distribution of processed transaction amounts")
                    .baseUnit("USD")
                    .register(meterRegistry);

            this.processingTimer = Timer.builder("fraud.transactions.processing-time")
                    .description("Transaction processing latency")
                    .register(meterRegistry);
        } else {
            this.transactionsCounter = null;
            this.fraudulentTransactionsCounter = null;
            this.transactionAmountSummary = null;
            this.processingTimer = null;
        }
    }

    public void recordTransaction(TransactionEvent event, boolean fraudulent, double fraudScore) {
        if (transactionsCounter != null) {
            transactionsCounter.increment();
        }

        if (transactionAmountSummary != null && event != null) {
            transactionAmountSummary.record(event.getAmount());
        }

        if (fraudulent && fraudulentTransactionsCounter != null) {
            fraudulentTransactionsCounter.increment();
        }
    }

    public void recordProcessingLatency(Duration duration) {
        if (duration != null && processingTimer != null) {
            processingTimer.record(duration.toNanos(), TimeUnit.NANOSECONDS);
        }
    }
}
