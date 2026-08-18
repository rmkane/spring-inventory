# Spring Inventory

A Spring Boot inventory service for products, stock, customers, carts, and orders. JSON lives on the resource paths; a Thymeleaf UI is at `/` and `/ui`.

## Requirements

- JDK 21
- Maven 3.9+
- Docker (for Postgres, or for the full stack)
- Git (for hooks)

## Quick start

Start Postgres, then the app:

```bash
make db-up
make run
```

Or build and run everything with Compose:

```bash
make up
```

Then open:

- UI: <http://localhost:8080>
- Swagger: <http://localhost:8080/swagger-ui.html>
- OpenAPI: <http://localhost:8080/v3/api-docs>

Default database is `inventory` / `inventory` / `inventory` on `localhost:5432`. Flyway applies schema and seed data on startup.

## Configuration

Override the datasource with environment variables:

| Variable | Default |
| - | - |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/inventory` |
| `SPRING_DATASOURCE_USERNAME` | `inventory` |
| `SPRING_DATASOURCE_PASSWORD` | `inventory` |

## HTTP API

CRUD on `/products`, `/customers`, `/inventory`, `/carts`, and `/orders`. Nested reads:

- `GET /customers/{id}/carts`
- `GET /customers/{id}/orders`

List endpoints return a page envelope. Query params:

| Param | Default | Notes |
| - | - | - |
| `page` | `1` | 1-based |
| `size` | `10` | Max `100` |
| `sort` | resource default | Allowlisted per resource |
| `dir` | `asc` or `desc` | |

Errors use `ProblemDetail` (`404`, `400`, `409`).

## UI

HTML pages share a header, nav, and footer:

| Path | Page |
| - | - |
| `/` | Dashboard |
| `/ui/products` | Product list and detail |
| `/ui/customers` | Customer list and detail |
| `/ui/inventory` | Inventory list |
| `/ui/carts` | Cart list and detail |
| `/ui/orders` | Order list and detail |

Lists include sortable headers and a pager (page size, range, page jump, prev/next).

## Development

```bash
make help          # all targets
make format        # Spotless apply
make lint          # Spotless check
make test          # unit tests
make integration   # integration tests (app on :8080)
make hooks         # install Git hooks
make debug         # JDWP on 8787 (DEBUG_PORT=... to override)
make db-down       # stop Postgres and drop the volume
make down          # stop Compose
```

Layering is controller → service → manager → repository. Domain types are Lombok `@Data` classes; request/response payloads are records.

## Tests

Unit tests live in `src/test/java` and run with `make test` / `mvn test`. Integration tests are under `src/integration/test/java`, tagged `integration`, compiled in the default build, and run with:

```bash
make integration
```

Those tests expect the app on `http://localhost:8080`.

CI (`.github/workflows/ci.yml`) runs Spotless, unit tests, and package on `main` / `develop`.

## Git hooks

Install once per clone:

```bash
./scripts/install_hooks.sh
```

or `make hooks`. That sets `core.hooksPath` to `.githooks`. Pre-commit runs Spotless, compile, and unit tests — the same checks as CI lint/test. If formatting fails, run `make format` and stage again.
