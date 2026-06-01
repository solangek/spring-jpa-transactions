# Spring JPA Transactions — Course Example

A small Spring Boot web app that demonstrates **Spring Data JPA** and, above all,
how **`@Transactional`** gives you *all-or-nothing* database operations.

It is a CRUD app for `User` records (name + email) with a Thymeleaf UI, plus two
special endpoints that show a transaction **committing** vs. **rolling back**.

---

## Tech stack

| Concern        | Choice                                             |
|----------------|----------------------------------------------------|
| Framework      | Spring Boot **4.0.6** (Spring Framework 7, Jakarta EE 11) |
| Language       | Java **21** (LTS)                                  |
| Persistence    | Spring Data JPA + Hibernate                        |
| Database       | MariaDB/MySQL (default) **or** H2 in-memory        |
| View layer     | Thymeleaf + Bootstrap 4 (CDN)                      |
| Build          | Maven (wrapper included: `./mvnw`)                 |

---

## Project layout

```
src/main/java/com/example/demo/
├── SpringJpaTransactions.java     # @SpringBootApplication entry point
├── controllers/UserController.java # web endpoints -> Thymeleaf views
├── services/UserService.java       # business logic; holds the @Transactional demo
└── repo/
    ├── User.java                   # JPA @Entity (one row per user)
    └── UserRepository.java         # JpaRepository -> free CRUD + derived queries

src/main/resources/
├── application.properties          # DB + JPA configuration (two DB options)
└── templates/                      # index / add-user / update-user HTML
```

The layering is the classic **Controller → Service → Repository → Database**:
the controller never talks to the database directly, and the `@Transactional`
boundary sits on the service method.

---

## Running the app

### Option A — H2 in-memory database (no setup)

1. In `application.properties`, comment out **OPTION A** and uncomment **OPTION B**.
2. Start it:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Open http://localhost:8080. (H2 console: http://localhost:8080/h2-console)

> Data is wiped on every restart — perfect for experimenting.

### Option B — MariaDB / MySQL via Docker (default)

1. Start the database container (details in [`README-MARIA-DB.md`](./README-MARIA-DB.md)):
   ```bash
   docker compose up -d
   ```
   This also starts phpMyAdmin at http://localhost:8081.
2. Run the app:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Open http://localhost:8080.

The container creates the `ex4` database, which matches
`spring.datasource.url` in `application.properties`.

---

## The transaction demo (the point of this example)

`UserService.addUsers(...)` is annotated `@Transactional`, so every `save()`
inside it runs in **one** database transaction.

| URL                                   | What it does                                                                 |
|---------------------------------------|------------------------------------------------------------------------------|
| http://localhost:8080/transactionOK   | Adds 3 valid users → the transaction **commits**, all 3 appear.              |
| http://localhost:8080/transactionBAD  | Adds 3 users, but the 3rd has an empty email (fails `@NotEmpty`).            |

For `/transactionBAD`, the validation error thrown while saving the 3rd user
rolls the whole transaction back — so **none** of the three are saved, not even
the first two valid ones. Remove the `@Transactional` annotation and retry to
see the difference: the first two would persist and only the third would fail.

Set `spring.jpa.show-sql=true` (already on) to watch the `INSERT` statements and
the rollback in the console.

---

## What changed in the Spring Boot 4 upgrade

This started life on Spring Boot 2.6. Key migration points (useful to know):

- **`javax.*` → `jakarta.*`** for all JPA, Validation and Transaction annotations.
- **Java 1.8 → Java 21**; Spring Boot 4 requires Java 17+.
- **MySQL driver** artifact renamed: `mysql:mysql-connector-java` → `com.mysql:mysql-connector-j`.
- **Let the parent BOM manage versions** — removed the hand-pinned `<version>` tags
  that previously mixed `2.6.7` and `2.7.0` starters.
- **Dropped the explicit Hibernate dialect** (`MySQL57Dialect` was removed); Hibernate 6
  auto-detects it.
- Switched to **Spring's `@Transactional`** and **constructor injection**, and removed
  unused dependencies/imports.

---

## Tests

```bash
./mvnw test
```

`Demo1ApplicationTests.contextLoads()` is a smoke test that simply verifies the
Spring context starts up correctly.