# 🚀 High-Throughput Inventory Tracker

A Spring Boot application that simulates a flash-sale system where thousands of users attempt to purchase a limited stock simultaneously. Uses Redis for distributed caching and atomic operations to prevent overselling.

---

## 🧩 Tech Stack

* Java 17+
* Spring Boot
* Spring Data Redis
* Redis (local or Docker)
* Maven
* JUnit 5 (for concurrency testing)

---

## ⚙️ Features

* Flash-sale simulation (high concurrency)
* Redis-based distributed cache
* Atomic stock decrement using Lua script
* Prevents overselling (race-condition safe)
* Concurrency stress test included

---

## 📁 Project Structure

```
src/
 ├── main/
 │   ├── java/.../controller
 │   ├── java/.../service
 │   ├── java/.../config
 │   └── resources/
 │        └── scripts/decrement_inventory.lua
 └── test/
     └── InventoryConcurrencyTest.java
```

---

## 🛠️ Setup

### 1. Start Redis

**Option A: Local**

```
redis-server
```

**Option B: Docker**

```
docker run -p 6379:6379 redis
```

---

### 2. Build & Run

```
mvn clean install
mvn spring-boot:run
```

App runs on:

```
http://localhost:8080
```

---

## 📡 API Usage

### Initialize Stock

```
POST /api/inventory/init?sku=flash-001&stock=500
```

### Check Stock

```
GET /api/inventory/flash-001
```

### Purchase

```
POST /api/inventory/purchase
Content-Type: application/json

{
  "sku": "flash-001",
  "quantity": 1
}
```

---

## 🧪 Concurrency Test

Run:

```
mvn test
```

Test simulates:

* 1000 purchase attempts
* 500 available stock
* ensures:

    * only 500 succeed
    * rest fail
    * no overselling

---

## 🧠 How It Works

* Stock is stored in Redis:

  ```
  inventory:item:{sku}
  ```

* Purchase uses a **Lua script** for atomic check + decrement:

    * prevents race conditions
    * runs entirely inside Redis

---

## ⚠️ Important Notes

* Lua script must be located at:

  ```
  src/main/resources/scripts/decrement_inventory.lua
  ```
* Redis must be running before app starts
* Thread pool size in tests should be < total requests (e.g., 200 vs 1000)

---

## 📈 Production Ideas

* Add Kafka for async order processing
* Add per-user purchase limits
* Add reservation timeout + rollback
* Add monitoring (Prometheus/Grafana)

---

## Summary

This project demonstrates how to:

* handle extreme concurrency safely
* use Redis as a distributed lock-free system
* prevent overselling in flash-sale scenarios

---
