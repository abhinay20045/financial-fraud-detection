package com.example.fraud.consumer.service;

import com.example.fraud.common.dto.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Service
public class RedisAggregationService {

    private static final Logger log = LoggerFactory.getLogger(RedisAggregationService.class);
    private static final Duration ACCOUNT_TTL = Duration.ofHours(6);

    private final StringRedisTemplate redisTemplate;

    public RedisAggregationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public AccountAggregate updateAggregate(TransactionEvent event) {
        if (redisTemplate == null) {
            return fallbackAggregate(event);
        }

        String key = key(event.getAccountId());

        try {
            redisTemplate.opsForHash().increment(key, "count", 1);
            redisTemplate.opsForHash().increment(key, "totalAmount", event.getAmount());
            redisTemplate.opsForHash().put(key, "lastAmount", Double.toString(event.getAmount()));
            redisTemplate.opsForHash().put(key, "lastTimestamp", event.getTimestamp().toString());
            redisTemplate.opsForHash().put(key, "lastLocation", safeString(event.getLocation()));
            redisTemplate.expire(key, ACCOUNT_TTL);

            Map<Object, Object> values = redisTemplate.opsForHash().entries(key);
            return toAggregate(event.getAccountId(), values);
        } catch (RuntimeException ex) {
            log.warn("Redis unavailable, using fallback aggregates: {}", ex.getMessage());
            return fallbackAggregate(event);
        }
    }

    public AccountAggregate getAggregateForAccount(String accountId) {
        if (redisTemplate == null) {
            return null;
        }

        try {
            Map<Object, Object> values = redisTemplate.opsForHash().entries(key(accountId));
            if (values.isEmpty()) {
                return null;
            }
            return toAggregate(accountId, values);
        } catch (RuntimeException ex) {
            log.warn("Redis unavailable while fetching aggregates: {}", ex.getMessage());
            return null;
        }
    }

    private AccountAggregate toAggregate(String accountId, Map<Object, Object> values) {
        long count = toLong(values.get("count"));
        double total = toDouble(values.get("totalAmount"));
        double average = count == 0 ? 0 : total / count;
        double lastAmount = toDouble(values.get("lastAmount"));
        Instant lastTimestamp = parseInstant(values.get("lastTimestamp"));
        String lastLocation = toStringValue(values.get("lastLocation"));

        return new AccountAggregate(
                accountId,
                count,
                total,
                average,
                lastAmount,
                lastTimestamp,
                lastLocation
        );
    }

    private AccountAggregate fallbackAggregate(TransactionEvent event) {
        Instant timestamp = event.getTimestamp() != null ? event.getTimestamp() : Instant.now();
        return new AccountAggregate(
                event.getAccountId(),
                1,
                event.getAmount(),
                event.getAmount(),
                event.getAmount(),
                timestamp,
                event.getLocation()
        );
    }

    private String key(String accountId) {
        return "fraud:acct:" + accountId;
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0;
        }
        return Long.parseLong(value.toString());
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0;
        }
        return Double.parseDouble(value.toString());
    }

    private Instant parseInstant(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Instant.parse(value.toString());
        } catch (Exception ex) {
            return null;
        }
    }

    private String toStringValue(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    private String safeString(String value) {
        return StringUtils.hasText(value) ? value : "UNKNOWN";
    }
}
