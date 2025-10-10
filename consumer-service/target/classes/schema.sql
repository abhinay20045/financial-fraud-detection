CREATE TABLE IF NOT EXISTS transactions
(
    id               SERIAL PRIMARY KEY,
    transaction_id   VARCHAR(64)  NOT NULL UNIQUE,
    account_id       VARCHAR(64)  NOT NULL,
    merchant_id      VARCHAR(128),
    location         VARCHAR(128),
    device_id        VARCHAR(64),
    currency         VARCHAR(3),
    amount           NUMERIC(19, 2) NOT NULL,
    event_timestamp  TIMESTAMPTZ   NOT NULL,
    processed_at     TIMESTAMPTZ   NOT NULL,
    fraud_score      DOUBLE PRECISION,
    is_fraud         BOOLEAN,
    fraud_reason     TEXT
);
