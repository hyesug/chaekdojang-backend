# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Test all
./gradlew test

# Single test class
./gradlew test --tests "com.chaekingam.api.SomeTest"

# Clean build
./gradlew clean build
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Stack

- **Java 21**, Spring Boot 3.5.x
- **Spring Data JPA** + PostgreSQL (driver declared, connection not yet configured in `application.yaml`)
- **Spring Security** (included, not yet configured)
- **Spring Validation**, Lombok
- Gradle 8 with Kotlin DSL (`build.gradle.kts`)

## Architecture

The project is in early scaffold stage. The root package is `com.chaekingam.api`.

Expected layered structure to build toward:
- `controller/` — REST controllers (`@RestController`)
- `service/` — business logic
- `repository/` — Spring Data JPA interfaces
- `entity/` or `domain/` — JPA entities
- `dto/` — request/response objects
- `config/` — Security, JPA, and other `@Configuration` classes

`application.yaml` currently only sets `spring.application.name: api`. Database URL, credentials, JPA dialect, and security settings still need to be added here (or in profile-specific `application-{profile}.yaml` files).

Spring Security is on the classpath and will block all requests by default until a `SecurityFilterChain` bean is configured.
