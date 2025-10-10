package com.example.fraud.consumer.config;

import com.example.fraud.common.dto.FraudAlert;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, FraudAlert> fraudAlertProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> producerProps = kafkaProperties.buildProducerProperties();
        producerProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        JsonSerializer<FraudAlert> serializer = new JsonSerializer<>();
        serializer.setAddTypeInfo(false);
        return new DefaultKafkaProducerFactory<>(producerProps, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, FraudAlert> fraudAlertKafkaTemplate(ProducerFactory<String, FraudAlert> fraudAlertProducerFactory) {
        return new KafkaTemplate<>(fraudAlertProducerFactory);
    }
}
