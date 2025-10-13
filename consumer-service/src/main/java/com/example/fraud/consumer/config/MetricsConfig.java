package com.example.fraud.consumer.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Adds consistent tags to all Micrometer metrics so Prometheus and Grafana
 * can group series from different instances.
 */
@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer(
            @Value("${INSTANCE_ID:local}") String instanceId) {
        return registry -> registry.config()
                .commonTags("instance", instanceId);
    }
}
