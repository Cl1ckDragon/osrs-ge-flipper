# OSRS GE Flipper

A full-stack web app that surfaces the best Grand Exchange flipping opportunities in Old School RuneScape. Live prices are fetched from the [OSRS Wiki API](https://prices.runescape.wiki/api/v1/osrs), scored by flip potential, and updated every 5 minutes.

**[Live demo →](https://osrs-ge-flipper.onrender.com)**

![OSRS GE Flipper screenshot](docs/screenshot.png)

---

## Features

- **Live flip scores** — margin × buy limit, calculated from 5-minute volume-weighted average prices (not single-transaction outliers)
- **Price history charts** — click any item to see its margin trend over the last 24 hours
- **Full-item search** — search across the entire OSRS item catalogue, not just the top results
- **JWT authentication** — register, log in, and receive a signed token
- **Rate limiting** — login capped at 5 attempts/min per IP, register at 3, via token-bucket algorithm
- **Item icons** — pulled directly from the OSRS Wiki

---

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.2 |
| Database | PostgreSQL 16 + Flyway migrations |
| Auth | JWT (HMAC-SHA256) via Spring Security OAuth2 Resource Server |
| Rate limiting | Bucket4j (token bucket, in-memory) |
| Frontend | React 18, TypeScript, Vite |
| Charts | Recharts |
| API docs | OpenAPI 3 / Swagger UI |
| DevOps | Docker, Docker Compose, GitHub Actions CI |
| Deployment | Render |

---

## Quick start

```bash
git clone https://github.com/your-username/osrs-ge-flipper.git
cd osrs-ge-flipper
cp .env.example .env
docker compose up --build
```

| Service | URL |
|---|---|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

> Requires [Docker Desktop](https://www.docker.com/products/docker-desktop). No other local dependencies needed.

---

## Architecture

```
Browser
  │
  ▼
nginx (frontend container)
  │  serves static React build
  │  proxies /api/* → backend
  ▼
Spring Boot (backend container)
  │  GET /api/prices          — live flip opportunities
  │  GET /api/prices/search   — full-catalogue item search
  │  GET /api/prices/history  — 24h margin history
  │  POST /api/auth/register  — create account
  │  POST /api/auth/login     — issue JWT
  ▼
PostgreSQL
  │  users
  └  price_snapshots (written every 5 min by @Scheduled job)
```

The backend is intentionally layered — controllers are thin, business logic lives in services, and the `FlipScoreCalculator` is a pure component with no Spring dependencies so it can be unit tested directly without mocking.

---

## API docs

Swagger UI is available at `/swagger-ui.html` on both local and the deployed backend:

**[https://osrs-ge-flipper-api.onrender.com/swagger-ui.html](https://osrs-ge-flipper-api.onrender.com/swagger-ui.html)**

---

## Running tests

```bash
cd backend
mvn test
```

Covers: flip score calculation (unit), price service filtering and sort order (Mockito), user registration and login including a security test that verifies both failure modes return the same error message to prevent username enumeration, and rate limiter bucket behaviour per IP.

---

## Known limitations

- **Rate limiting is in-memory** — buckets reset on restart and are not shared across multiple instances. A Redis-backed store would be needed for a multi-instance deployment.
- **Render free tier cold starts** — the backend spins down after 15 minutes of inactivity. First request after sleep takes ~30 seconds.
- **Price snapshots require uptime** — history charts only show data from when the app has been running. Snapshots are not backfilled on startup.
