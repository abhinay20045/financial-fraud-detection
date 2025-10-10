package com.example.fraud.consumer.listener;

import com.example.fraud.common.constants.Topics;
import com.example.fraud.common.dto.TransactionEvent;
import com.example.fraud.consumer.service.TransactionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final TransactionService transactionService;

    public TransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = Topics.TRANSACTIONS,
            groupId = "fraud-detector-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void handleTransaction(TransactionEvent event) {
        transactionService.process(event);
    }
}
