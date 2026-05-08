# 🚀 Spring Boot Redis Guardrail API

A high-performance microservice with Redis-based atomic locks, virality scoring, and smart notification batching.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2 |
| Database | PostgreSQL 15 |
| Cache / Guardrails | Redis 7 |
| Infra | Docker + Docker Compose |

---

## How to Run

### Step 1 — Start PostgreSQL + Redis + pgAdmin
```bash
docker-compose up -d
```

### Step 2 — Run the Spring Boot App
```bash
mvn spring-boot:run
```

App runs on: `http://localhost:8080`
pgAdmin UI: `http://localhost:5050` (login: admin@admin.com / admin)

---

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/users` | Create a user |
| POST | `/api/bots` | Create a bot |
| POST | `/api/posts` | Create a post |
| POST | `/api/posts/{id}/comments` | Add a comment |
| POST | `/api/posts/{id}/like` | Like a post |
| GET | `/api/posts/{id}/stats` | Get virality score + bot count |

---

## How Thread Safety is Guaranteed (Phase 2 - Atomic Locks)

### The Problem
In a concurrent system, 200 bot requests arrive at the same millisecond.  
Without atomic operations, two threads could both read `botCount = 99`, both pass the check, both increment → `botCount = 101`. The cap is broken.

### The Solution — Lua Script in Redis

```lua
local current = tonumber(redis.call('GET', KEYS[1]) or '0')
if current >= tonumber(ARGV[1]) then
    return 0
end
redis.call('INCR', KEYS[1])
return 1
```

**Redis executes Lua scripts as a single atomic command.** No two threads can interleave. The check-then-increment is guaranteed to be atomic — making it impossible to exceed 100 bot replies.

### Cooldown Cap — SET NX
```
SET cooldown:bot_1:human_1 "1" NX EX 600
```
`NX` = Set only if Not eXists. This is also atomic — exactly one bot wins the race.

### Stateless Design
- ❌ No HashMap, no static variables, no Java memory state
- ✅ All counters → Redis (`post:{id}:bot_count`)
- ✅ All cooldowns → Redis with TTL (`cooldown:bot_{id}:human_{id}`)
- ✅ All pending notifications → Redis List (`user:{id}:pending_notifs`)
- ✅ PostgreSQL only stores committed, guardrail-approved content

---

## Redis Keys Reference

| Key Pattern | Purpose | TTL |
|-------------|---------|-----|
| `post:{id}:virality_score` | Virality points for a post | None |
| `post:{id}:bot_count` | Number of bot replies to a post | None |
| `cooldown:bot_{id}:human_{id}` | Bot→Human interaction cooldown | 10 min |
| `notif_cooldown:user_{id}` | Notification send cooldown | 15 min |
| `user:{id}:pending_notifs` | Buffered notification messages (Redis List) | Cleared by sweeper |

---

## Virality Score Rules

| Interaction | Points |
|-------------|--------|
| Bot Reply | +1 |
| Human Like | +20 |
| Human Comment | +50 |

---

## CRON Sweeper

Runs every **5 minutes** (`@Scheduled(cron = "0 */5 * * * *")`).

- Scans all `user:*:pending_notifs` keys in Redis
- Pops all buffered messages per user
- Logs: `"Summarized Push Notification: Bot X and [N] others interacted with your posts."`
- Clears the Redis list

---

## Project Structure

```
src/main/java/com/example/demo
│
├── config
│   └── RedisConfig
│
├── controller
│   ├── BotController
│   ├── HealthController
│   ├── PostController
│   └── UserController
│
├── dto
│   └── dtos
│
├── entity
│   ├── Bot
│   ├── Comment
│   ├── Post
│   └── User
│
├── exception
│   └── GlobalExceptionHandler
│
├── mapper
│
├── repository
│   ├── BotRepository
│   ├── CommentRepository
│   ├── PostRepository
│   └── UserRepository
│
├── service
│   ├── impl
│   │   └── PostServiceImpl
│   │
│   ├── PostService
│   └── RedisGuardrailService
│
├── scheduler
│   └── Notification
│
├── util
│
└── DemoApplication
```
