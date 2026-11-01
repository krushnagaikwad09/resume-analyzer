# Pharmacy Inventory Management API

A Spring Boot REST API for batch-wise pharmacy stock management. Sales use
First-Expire-First-Out (FEFO), never sell expired batches, and record exactly
which batches supplied each sale.

## Requirements

- Java 21
- Docker Desktop (for the local MySQL database)

## Run locally

From the repository root, start MySQL:

```powershell
docker compose -f pharmacy-inventory/docker-compose.yml up -d
```

Run the API:

```powershell
.\mvnw.cmd -f pharmacy-inventory/pom.xml spring-boot:run
```

The API listens on `http://localhost:8080`. Local database credentials are
development-only. Override them with `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`
when using another database; do not use the example credentials in production.

## API

Create a medicine:

```http
POST /api/medicines
Content-Type: application/json

{"sku":"MED-001","name":"Amoxicillin","description":"500 mg capsules"}
```

Add a received batch (expiry date uses ISO `YYYY-MM-DD`):

```http
POST /api/medicines/1/batches
Content-Type: application/json

{"batchNumber":"LOT-2026-01","expiryDate":"2027-03-31","quantityReceived":100}
```

List medicines and a medicine's batches:

```http
GET /api/medicines
GET /api/medicines/1/batches
```

Sell stock; the requested amount is allocated across eligible batches by
earliest expiry date. If eligible stock is insufficient, the entire sale fails
with HTTP `409` and stock is unchanged.

```http
POST /api/medicines/1/sales
Content-Type: application/json

{"quantity":12}
```

List in-stock batches expiring within the selected alert window. `days` is
restricted to 15 through 30 and defaults to 30:

```http
GET /api/batches/expiring
GET /api/batches/expiring?days=15
```

## Tests

The automated tests use an in-memory H2 database and cover FEFO allocation,
expired-batch exclusion, atomic insufficient-stock handling, and expiry alerts.

```powershell
.\mvnw.cmd -f pharmacy-inventory/pom.xml test
```
