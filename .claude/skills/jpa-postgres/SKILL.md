---
name: jpa-postgres
description: "JPA 엔티티, PostgreSQL 테이블/스키마, 리포지토리 쿼리를 만들거나 수정할 때 사용. '~엔티티 만들어줘', '테이블 추가해줘', '쿼리 짜줘' 같은 요청에 적용."
---

# JPA + PostgreSQL 데이터 계층 표준 (jpa-postgres)

Spring Data JPA + PostgreSQL로 데이터 계층을 만들 때 따르는 표준.

## 네이밍
- 엔티티 클래스: `PascalCase` 단수형 (예: `Employee`).
- 테이블명: 스네이크케이스 소문자로 고정한다(예: `employee`, `department`). 필요하면 `@Table(name = "...")`으로 명시한다.
- 컬럼명도 스네이크케이스 소문자로 고정한다(`@Column(name = "...")`로 명시하거나 기본 네이밍 전략에 맡긴다).
- 기본키 컬럼명은 `id`로 통일한다.

## 공통 감사 컬럼
- 모든 엔티티에 생성/수정 이력을 남긴다: `created_at`, `updated_at` (필요 시 `created_by`, `updated_by`).
- Spring Data JPA Auditing(`@EnableJpaAuditing`, `@EntityListeners(AuditingEntityListener.class)`, `@CreatedDate`, `@LastModifiedDate`)을 사용해 애플리케이션 레이어에서 자동으로 채운다.
- 베이스 엔티티(`@MappedSuperclass`)로 감사 컬럼을 공통화하고, 매 엔티티마다 중복 선언하지 않는다.

## 기본키
- 기본키는 `Long id` + `@GeneratedValue(strategy = GenerationType.IDENTITY)` (PostgreSQL `bigserial`/`identity`)를 기본으로 사용한다.
- 외부에 노출되는 식별자가 추측 가능하면 안 되는 경우에만 UUID 도입을 검토하고, 이때는 반드시 스펙/계획에 먼저 명시하고 승인받는다.

## 검증 vs 제약조건
- Bean Validation(`@NotNull`, `@Size` 등)은 DTO/입력 단에서 사용자 입력을 막는 용도.
- DB 제약조건(`NOT NULL`, `UNIQUE`, `CHECK`)은 데이터 무결성의 최종 방어선으로 엔티티 매핑에도 반영한다(`nullable = false`, `unique = true` 등). 둘 중 하나만 두지 않는다.

## 스키마 관리
- `spring.jpa.hibernate.ddl-auto`는 로컬 개발 편의를 위해서만 `update`를 임시로 쓸 수 있지만, 스키마가 안정화되면 마이그레이션 도구(Flyway 등) 도입 여부를 스펙/계획 단계에서 사용자와 논의해 결정한다. 임의로 프로덕션향 자동 스키마 변경에 의존하지 않는다.
- 연관관계(예: 직원-부서)는 실제 요구사항에 필요한 범위에서만 매핑하고, `fetch` 전략(기본은 `LAZY` 권장)을 명시적으로 지정한다.

## 리포지토리
- `interface XxxRepository extends JpaRepository<Xxx, Long>` 형태를 기본으로 하고, 조회 조건은 메서드 이름 규칙(예: `findByStatus`, `findByNameContaining`)으로 우선 표현한다.
- 복잡한 동적 검색(예: 이름·부서·상태 복합 필터)이 필요하면 `Specification` 또는 `@Query`를 사용하되, 서비스 계층에서만 호출한다.

## 하지 않는 것
- 서비스에서 `EntityManager`/네이티브 SQL을 임의로 직접 다루기 (리포지토리 계층을 우회)
- 스펙에 없는 컬럼·테이블 임의 추가
- 감사 컬럼 생략

## 관련
- 백엔드: `spring-backend`