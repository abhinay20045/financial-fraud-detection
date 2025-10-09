package com.example.fraud.producer.service;

import com.example.fraud.common.constants.Topics;
import com.example.fraud.common.dto.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private final TransactionGenerator generator;

    // Automatically simulate one transaction every 2 seconds
    @Scheduled(fixedRate = 2000)
    public void sendFakeTransaction() {
        TransactionEvent event = generator.generateTransaction();
        kafkaTemplate.send(Topics.TRANSACTIONS, event.getAccountId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ Sent transaction: {} -> ${} [{}]",
                                event.getTransactionId(),
                                event.getAmount(),
                                event.getLocation());
                    } else {
                        log.error("❌ Failed to send transaction {}", event.getTransactionId(), ex);
                    }
                });
    }
}
