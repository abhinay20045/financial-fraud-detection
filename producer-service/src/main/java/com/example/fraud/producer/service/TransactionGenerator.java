package com.example.fraud.producer.service;

import com.example.fraud.common.dto.TransactionEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class TransactionGenerator {

    private static final List<String> LOCATIONS = List.of("New York", "San Francisco", "Chicago", "Boston", "Mumbai", "London");
    private static final List<String> MERCHANTS = List.of("Amazon", "Starbucks", "Walmart", "Uber", "Netflix", "Target");
    private static final List<String> ACCOUNTS = List.of("A100", "A200", "A300", "A400", "A500");
    private static final List<String> DEVICES = List.of("D1", "D2", "D3", "D4");
    private static final Random RANDOM = new Random();

    public TransactionEvent generateTransaction() {
        String account = ACCOUNTS.get(RANDOM.nextInt(ACCOUNTS.size()));
        String merchant = MERCHANTS.get(RANDOM.nextInt(MERCHANTS.size()));
        String location = LOCATIONS.get(RANDOM.nextInt(LOCATIONS.size()));
        String device = DEVICES.get(RANDOM.nextInt(DEVICES.size()));

        // Normal spending pattern with occasional outlier
        double amount = RANDOM.nextDouble() < 0.95
                ? 10 + RANDOM.nextDouble() * 300 // normal
                : 2000 + RANDOM.nextDouble() * 8000; // potential fraud spike

        TransactionEvent event = new TransactionEvent();
        event.setTransactionId(UUID.randomUUID().toString());
        event.setAccountId(account);
        event.setMerchantId(merchant);
        event.setLocation(location);
        event.setDeviceId(device);
        event.setAmount(amount);
        event.setTimestamp(Instant.now());
        return event;
    }
}
