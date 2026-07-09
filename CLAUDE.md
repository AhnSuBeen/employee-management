# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development workflow

This is a local, git-free assignment — no version control, branches, or PRs. The workflow is:
spec (`docs/specs/`) → plan (`docs/plans/`) → explicit user approval → implementation → local
verification. No code changes before an approved plan, and no scope beyond what the spec covers.
Full rules: `.claude/rules/development-workflow.md`.

### Skill catalog (on-trigger, `.claude/skills/<name>/SKILL.md`)

| Do this | Skill | Example trigger |
|---|---|---|
| Write a plan before coding | `writing-plans` | "계획서부터 써줘" |
| Execute an approved plan | `executing-plans` | "계획대로 진행해" |
| Controller/service/DTO/validation work | `spring-backend` | "~API 만들어줘" |
| Thymeleaf pages/forms | `thymeleaf-ui` | "~페이지 추가해줘" |
| JPA entities / PostgreSQL schema | `jpa-postgres` | "~엔티티 만들어줘" |
| Self-review a change before calling it done | `code-review-standard` | "이 변경 검토해줘" |

## Project state

This is a Spring Boot project scaffolded from Spring Initializr and not yet built out — the only application code is the `EmployeeManagementApplication` entry point. There are no controllers, entities, repositories, or services yet. `application.yaml` only sets `spring.application.name`; no datasource is configured even though the PostgreSQL driver is a runtime dependency, so a local Postgres connection (or an override via env vars / `application-*.yaml`) must be added before JPA features will work.

## Commands

Use the Gradle wrapper (`gradlew.bat` on this Windows machine) — do not assume a global `gradle` install.

```powershell
.\gradlew.bat build          # compile, run tests, assemble jar
.\gradlew.bat bootRun         # run the application locally
.\gradlew.bat test            # run the full test suite (JUnit 5 / useJUnitPlatform)
.\gradlew.bat test --tests "com.example.employeemanagement.EmployeeManagementApplicationTests"   # run a single test class
.\gradlew.bat test --tests "*.EmployeeManagementApplicationTests.contextLoads"                    # run a single test method
```

## Stack and architecture

- Java 21 (via Gradle toolchain), Spring Boot 3.5.16, group/package root `com.example.employeemanagement`.
- Dependencies are in place for a typical layered Spring MVC app but not yet wired up:
  - `spring-boot-starter-web` + `spring-boot-starter-thymeleaf` for server-rendered MVC views.
  - `spring-boot-starter-data-jpa` + `postgresql` (runtime) for persistence — expect entities/repositories under `com.example.employeemanagement` once added.
  - `spring-boot-starter-validation` for bean validation on request/DTO objects.
  - Lombok is available at compile time only (`compileOnly` + `annotationProcessor`), including for tests.
  - `spring-boot-devtools` is a development-only dependency (auto-restart on classpath changes).
- As the codebase grows, prefer the conventional Spring Boot package-by-layer or package-by-feature structure consistent with whatever the first real feature establishes — check existing packages under `src/main/java/com/example/employeemanagement` before introducing a new layout.
