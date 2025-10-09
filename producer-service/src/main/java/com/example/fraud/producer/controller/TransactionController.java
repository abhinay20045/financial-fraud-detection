package com.example.fraud.producer.controller;

import com.example.fraud.producer.service.KafkaProducerService;
import com.example.fraud.producer.service.TransactionGenerator;
import com.example.fraud.common.dto.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final KafkaProducerService producerService;
    private final TransactionGenerator generator;

    @PostMapping("/send")
    public String sendOneTransaction() {
        TransactionEvent event = generator.generateTransaction();
        producerService.sendFakeTransaction(); // triggers one send
        return "✅ Sent manual transaction";
    }
}
