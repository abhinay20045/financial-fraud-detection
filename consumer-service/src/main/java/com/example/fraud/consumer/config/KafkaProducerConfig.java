package com.example.fraud.consumer.config;

import com.example.fraud.common.dto.FraudAlert;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, FraudAlert> fraudAlertProducerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.getProducer().buildProperties(null));
    }

    @Bean
    public KafkaTemplate<String, FraudAlert> fraudAlertKafkaTemplate(
            ProducerFactory<String, FraudAlert> fraudAlertProducerFactory) {
        return new KafkaTemplate<>(fraudAlertProducerFactory);
    }
}
