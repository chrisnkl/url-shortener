# ShortenUrl - URL Shortening Service

A production-grade URL shortening service demonstrating advanced architectural patterns and best software engineer practices.

## Architecture Highlights

### Hexagonal Architecture (Ports & Adapters)
The application strictly follows hexagonal architecture principles to ensure:
- **Business Logic Isolation**: Domain logic is completely decoupled from infrastructure concerns
- **Testability**: Service dependencies are injected through ports, enabling easy mocking and testing
- **Framework Agnosticism**: The core domain can be adapted to different frameworks without modification
- **Clear Boundaries**: Dependencies flow inward toward the domain

### Two-Tier Caching Strategy
Implements a sophisticated caching layer for optimal performance:
- **L1 Cache (In-Memory)**: Caffeine cache for ultra-fast local lookups
- **L2 Cache (Distributed)**: Redis for shared cache across multiple instances
- **Fallback Chain**: L1 → L2 → Database
- **Cache Consistency**: Automatic synchronization between tiers

### Outbox Pattern for Reliable Event Publishing
Ensures **exactly-once** event delivery without distributed transactions:
- **Transactional Writes**: URL creation and event recording happen in the same database transaction
- **No Message Loss**: Events are persisted before being published
- **Replay Safety**: Failed publishes can be retried without duplication
- **Event Driven**: Decouples analytics from core business logic

### Idempotency for Fault Tolerance
First-class support for idempotent requests:
- **Request Deduplication**: Identified by `Idempotency-Key` header
- **Cached Responses**: Identical requests return the same response without side effects
- **Essential for Distributed Systems**: Handles network timeouts and client retries safely
- **Automatic Key Generation**: If client doesn't provide a key, server generates one for the response

```bash
# First request
POST /api/v1/urls -H "Idempotency-Key: key-123"
→ Creates short URL, returns Idempotency-Key

# Retry with same key
POST /api/v1/urls -H "Idempotency-Key: key-123"
→ Returns cached response, no new short URL created
```

### Unique ID Generation with Snowflake Algorithm
Uses Twitter's Snowflake algorithm for URL aliases:
- **Distributed-Friendly**: Works across multiple instances without coordination
- **Time-Ordered**: IDs encode timestamp information for better indexing
- **Collision-Free**: 64-bit IDs with extremely low collision probability
- **Performance**: Generated in-memory without database hits

## Core Features

### Create Short URL
```bash
POST /api/v1/urls
Content-Type: application/json
Idempotency-Key: unique-request-id

{
  "originalUrl": "https://example.com/very/long/path",
  "ttlInSeconds": 2592000  # Optional, defaults to 365 days
}

# Response
{
  "alias": "a1b2c3d4",
  "shortUrl": "http://localhost:8080/api/v1/urls/a1b2c3d4",
  "originalUrl": "https://example.com/very/long/path",
  "expiresAt": "2026-06-07T00:49:11Z"
}
```

### Redirect to Original URL
```bash
GET /api/v1/urls/{alias}
→ 302 Found with Location header
→ Logs analytics event (user-agent, IP) to outbox
```

## Technology Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Web** | Spring Boot 4.0.6, Jakarta Validation | HTTP handling & validation |
| **Caching** | Caffeine (L1), Redis (L2) | Multi-tier caching |
| **Persistence** | PostgreSQL, Spring Data JPA | Durable storage |
| **Messaging** | Apache Kafka, Outbox Pattern | Event streaming at scale |
| **ID Generation** | Snowflake Algorithm | Unique alias generation |
| **Database Evolution** | Hibernation DDL Auto | Schema management |
| **Development** | Lombok | Reduce boilerplate |

## Running the Application

### Prerequisites
```bash
docker-compose up -d  # Start PostgreSQL, Redis, Kafka
```

### Build & Run
```bash
./mvnw clean package
./mvnw spring-boot:run
```

---
