package com.example.fraud.consumer.service;

import com.example.fraud.common.constants.Topics;
import com.example.fraud.common.dto.FraudAlert;
import com.example.fraud.common.dto.TransactionEvent;
import com.example.fraud.consumer.metrics.FraudMetrics;
import com.example.fraud.consumer.model.TransactionEntity;
import com.example.fraud.consumer.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository repository;
    private final RedisAggregationService aggregationService;
    private final FraudDetectionService fraudDetectionService;
    private final FraudMetrics metrics;
    private final KafkaTemplate<String, FraudAlert> fraudAlertKafkaTemplate;

    public TransactionService(TransactionRepository repository,
                              RedisAggregationService aggregationService,
                              FraudDetectionService fraudDetectionService,
                              FraudMetrics metrics,
                              KafkaTemplate<String, FraudAlert> fraudAlertKafkaTemplate) {
        this.repository = repository;
        this.aggregationService = aggregationService;
        this.fraudDetectionService = fraudDetectionService;
        this.metrics = metrics;
        this.fraudAlertKafkaTemplate = fraudAlertKafkaTemplate;
    }

    @Transactional
    public void process(TransactionEvent event) {
        if (event == null) {
            log.warn("Skipped processing because event was null");
            return;
        }

        Optional<TransactionEntity> existing = repository.findByTransactionId(event.getTransactionId());
        if (existing.isPresent()) {
            log.debug("Transaction {} already processed, skipping duplicate", event.getTransactionId());
            return;
        }

        Instant start = Instant.now();

        AccountAggregate aggregate = aggregationService.updateAggregate(event);
        FraudDetectionService.FraudEvaluation evaluation = fraudDetectionService.evaluate(event, aggregate);

        TransactionEntity entity = toEntity(event, evaluation);
        repository.save(entity);

        metrics.recordTransaction(event, evaluation.isFraudulent(), evaluation.getScore());
        metrics.recordProcessingLatency(Duration.between(start, Instant.now()));

        if (evaluation.isFraudulent()) {
            FraudAlert alert = new FraudAlert(
                    event.getTransactionId(),
                    event.getAccountId(),
                    evaluation.getScore(),
                    true,
                    Instant.now(),
                    evaluation.getReason()
            );
            fraudAlertKafkaTemplate.send(Topics.FRAUD_ALERTS, event.getAccountId(), alert);
            log.warn("Flagged transaction {} as fraud: {}", event.getTransactionId(), evaluation.getReason());
        } else {
            log.info("Processed transaction {} score={} account={}",
                    event.getTransactionId(), evaluation.getScore(), event.getAccountId());
        }
    }

    private TransactionEntity toEntity(TransactionEvent event, FraudDetectionService.FraudEvaluation evaluation) {
        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId(event.getTransactionId());
        entity.setAccountId(event.getAccountId());
        entity.setMerchantId(event.getMerchantId());
        entity.setLocation(event.getLocation());
        entity.setDeviceId(event.getDeviceId());
        entity.setCurrency(event.getCurrency());
        entity.setAmount(event.getAmount());
        entity.setEventTimestamp(event.getTimestamp() != null ? event.getTimestamp() : Instant.now());
        entity.setProcessedAt(Instant.now());
        entity.setFraudScore(evaluation.getScore());
        entity.setFraud(evaluation.isFraudulent());
        entity.setFraudReason(evaluation.getReason());
        return entity;
    }
}
