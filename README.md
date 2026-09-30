# Webshop Orders

A Spring Boot REST API that stores web shop orders and their items in PostgreSQL and returns the list of orders, each with its items, as JSON.

## Tech stack

- Java 21, Spring Boot 4.1
- PostgreSQL 18 (Docker)
- Spring Data JPA / Hibernate
- Flyway for database migrations
- JUnit 5, MockMvc and Testcontainers for tests
- Gradle (wrapper included)

## Prerequisites

- **Java 21**
- **Docker**, running. It is needed both for running the application and for the tests.

Gradle does not need to be installed. The Gradle wrapper (`gradlew`) downloads the correct version.

## Running the application

```bash
./gradlew bootRun
```

This command:

1. starts a PostgreSQL container with Docker Compose (`compose.yaml`),
2. creates the database schema with Flyway,
3. loads sample data (seven orders in all statuses),
4. starts the API on port **8080**.

Open the order list: <http://localhost:8080/api/orders>

`bootRun` activates the `dev` profile. This profile loads the sample data and logs the executed SQL statements.

The development database is available on `localhost:15432` for database tools. The database name, user and password are in `compose.yaml`.

### Running from an IDE

`bootRun` sets two options automatically. When the application is started directly from the IDE, add them to the run configuration:

- VM option: `-Duser.timezone=UTC`
- Active profile: `dev` (only needed for sample data and SQL logging)

## Running the tests

```bash
./gradlew build
```

The integration tests run against a real PostgreSQL database in a temporary Docker container (Testcontainers). The tests run without the `dev` profile, so the database starts empty. One test checks that the sample data is not loaded outside the `dev` profile.

## API

### `GET /api/orders`

Returns all orders with their items. The newest orders come first.

Response `200 OK`:

```json
{
  "data": [
    {
      "id": "4bb55d97-e621-42cd-bb62-0d46e0b04127",
      "status": "NEW",
      "createdAt": "2026-09-29T19:48:00Z",
      "items": [
        {
          "name": "Kontoritool",
          "quantity": 1,
          "unitPrice": 249.00
        }
      ]
    }
  ]
}
```

- `id` is the order number (UUID).
- `status` is one of `NEW`, `PAID`, `SHIPPED`, `DELIVERED`, `CANCELLED`.
- `createdAt` is always in UTC (ISO-8601).
- `unitPrice` is a JSON number with two decimal places, in euros.

## Assumptions

- The currency is EUR.
- The order number is the UUID `id`. A human-readable order number can be added later as a separate field without breaking the API.
- Orders have a fixed set of statuses. Rules for changing a status are not part of this task.
- Orders are sorted by creation time, newest first. Orders with the same creation time are sorted by `id`, so the order of the list is always the same.
- Items are returned in the order they were added (by line number). The line number is not part of the response, because the position in the list already shows it.
- The same product name can appear more than once in one order. Such lines are not merged.
- All times are stored and returned in UTC.

## Design decisions

- **The database schema is owned by Flyway.** Hibernate only validates at startup that the entities match the tables (`ddl-auto=validate`). It never changes the schema.
- **One owner for each rule.** The database enforces data integrity: primary and foreign keys, `NOT NULL`, allowed statuses, positive quantities and prices. Business limits, such as a maximum price, belong in the application, because they can change.
- **PostgreSQL everywhere.** Development and tests use the same database as production (Docker Compose and Testcontainers), instead of an in-memory database with different behaviour.
- **Package by feature.** All order code is in the `order` package. Classes that are not needed outside the package are package-private.
- **Items belong to their order.** Items are only saved and loaded through their order (unidirectional one-to-many with cascade). There is no separate repository for items.
- **One query for the order list.** Orders and items are loaded in a single SQL query (fetch join). This avoids the N+1 problem. The SQL log confirms it: the default `findAll()` needed 8 queries for seven orders, the fetch join needs 1.
- **Entities do not leave the service layer.** The service converts entities to response objects (records) inside the transaction. Open-session-in-view is disabled, so database access only happens in the service.
- **Wrapper object in the response.** The list is inside a `data` field. Pagination information can be added later next to it without breaking existing clients.
- **IDs and creation time.** The application generates UUIDs for the rows it inserts. The database also has a UUID default for rows inserted with plain SQL. The creation time is set by the database.
- **Sample data only in development.** The sample data is in a separate Flyway location (`db/seed`, versions from 1000) that only the `dev` profile uses.
- **The application runs in UTC.** On a Windows machine, Java reported the legacy time zone ID `Europe/Kiev`. The PostgreSQL JDBC driver sends this ID to the database, and the PostgreSQL 18 image rejected it. Running the JVM in UTC makes the application independent of the time zone of the host machine.

## Production considerations

The packaged JAR does not include the Docker Compose support. In production the configuration comes from the environment:

- the database connection from `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD` (the password from a secrets store),
- the JVM time zone set to UTC in the image or the deployment,
- no `dev` profile, so no sample data is loaded.

## Troubleshooting

- **Docker is not running:** the application and the tests cannot start the database. Start Docker and try again.
- **Port 8080 or 15432 is in use:** stop the other program, or change the port (`server.port` for the application, `ports` in `compose.yaml` for the database).
- **A new schema migration is not applied locally:** the local database already has the sample data migration (version 1000), and Flyway does not apply lower versions by default. Recreate the local database with `docker compose down`.

## Not yet implemented / future work

- `POST /api/orders` for creating orders, with input validation and error responses (in progress)
- Pagination (with a two-query approach, so that items are still loaded without N+1)
- A human-readable order number
- Rules for status changes
- Order and line totals
- An index on the creation time for the default sort order
- OpenAPI documentation

## AI assistance

I used Claude (Anthropic) to plan the design, to review my code and to explain concepts. Some parts were drafted by Claude; these commits are marked with `Co-Authored-By`. I reviewed and tested all code.
