# spare-api

`spare-api` is a small Spring Boot service meant to **complement** `stock-api` and `core-api` without modifying them.
It stores stock-management metadata that is not part of the stock ledger (warehouse layout/bin mapping, product locations, etc.).

## What It Does (v1)
- Persist a warehouse layout per agency (`/warehouses/{agencyId}/layout`)
- Persist product → bin assignments (`/warehouses/{agencyId}/product-locations/{productId}`)

## What It Does (v2+)
- Location stock policies per bin/product (min/max/safety/ROP/cycle count)
- Reservations (allocated stock) + availability (on-hand from stock-api minus reserved from spare-api)
- Suppliers directory + supplier↔product metadata (lead time, MOQ, preferred)
- Reorder recommendations (computed from stock-api movements/levels + spare-api policies/reservations)
- Basic workflow requests (approval queue primitives)

All business requests require:
- `Authorization: Bearer <jwt>`
- `X-Tenant-ID: <organization_uuid>`

JWT is validated using the same HS256 secret as `core-api` / `stock-api`.

## Run Locally

1. Create a DB (example):
```sql
CREATE DATABASE comops_spare;
```

2. Start:
```bash
mvn spring-boot:run
```

Default port: `8082`

## Env Vars
- `APP_JWT_SECRET` (must match `app.jwt.secret` used by core/stock)
- `SPRING_R2DBC_URL` (default `r2dbc:postgresql://localhost:5432/comops_spare`)
- `SPRING_R2DBC_USERNAME`
- `SPRING_R2DBC_PASSWORD`
- `SPRING_DATASOURCE_URL` (JDBC URL for Liquibase)
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_STOCK_BASE_URL` (default `http://localhost:8081`)
