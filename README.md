# 🏦 Financial Fraud Detection System

A distributed, real-time **Financial Fraud Detection System** designed to identify and flag suspicious transactions using modern data infrastructure and observability tooling. The system leverages **Spring Boot microservices**, **Apache Kafka** for streaming, **Redis** for caching, **PostgreSQL** for persistent storage, and **Prometheus + Grafana** for monitoring and visualization.

---

## 🚀 Architecture Overview

### **Core Components**
1. **Producer Service**  
   - Simulates incoming transaction streams and publishes them to Kafka topics.  
   - Configurable producer settings (`acks=all`, `linger.ms`, `compression=zstd`) for reliability and throughput.

2. **Consumer Service (Fraud Detection Service)**  
   - Subscribes to Kafka topics and applies fraud detection logic.  
   - Uses statistical models and rule-based heuristics to detect anomalies.  
   - Writes flagged transactions to PostgreSQL.

3. **Common Library (`common-lib`)**  
   - Shared DTOs, configurations, and utilities used across services.  
   - Includes Kafka configuration, data models, and constants.

4. **Database (PostgreSQL)**  
   - Stores transaction data, fraud results, and metadata.  
   - Schema includes fields like `amount`, `merchant_id`, `fraud_score`, and `fraud_reason`.

5. **Redis Cache**  
   - Used for caching frequent queries (e.g., merchant risk scores) and state management between detection cycles.

6. **Prometheus & Grafana Stack**  
   - Prometheus scrapes metrics from Spring Boot actuator endpoints.  
   - Grafana visualizes metrics such as:
     - Kafka message throughput  
     - Fraud detection latency  
     - Redis cache hit ratio  
     - Database query performance  

7. **Docker & Docker Compose**  
   - Spins up the entire stack (Kafka, Zookeeper, Redis, PostgreSQL, Prometheus, Grafana, services).  
   - Enables local orchestration for end-to-end testing.

---

## 🧠 Features

- ✅ **Real-time Fraud Detection** with Kafka Streams and Spring Boot  
- ⚡ **High-Throughput Kafka Producers** with optimized configuration  
- 🔄 **Idempotent Processing** to prevent duplicate alerts  
- 💾 **Persistent Storage** using PostgreSQL  
- 🔍 **Caching & Performance Optimization** with Redis  
- 📊 **Observability Stack** powered by Prometheus and Grafana  
- 🧩 **Modular Architecture** via Maven multi-module structure (`producer-service`, `consumer-service`, `common-lib`)

---

## 🛠️ Tech Stack

| Category | Technologies |
|-----------|---------------|
| Backend Services | Spring Boot, Maven |
| Messaging | Apache Kafka, Zookeeper |
| Caching | Redis |
| Database | PostgreSQL |
| Monitoring | Prometheus, Grafana |
| Containerization | Docker, Docker Compose |
| Build & Deployment | Maven, Docker CLI |
| Language | Java 17 |

---

## ⚙️ Setup Instructions

### **1️⃣ Clone the Repository**
```bash
git clone https://github.com/your-username/financial-fraud-detection.git
cd financial-fraud-detection