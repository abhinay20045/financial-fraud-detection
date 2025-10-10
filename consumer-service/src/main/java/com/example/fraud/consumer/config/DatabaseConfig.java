package com.example.fraud.consumer.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example.fraud.consumer.repository")
@EntityScan(basePackages = "com.example.fraud.consumer.model")
public class DatabaseConfig {
}
