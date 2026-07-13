# 직원 테이블 스키마를 schema.sql DDL로 관리

## 문제 / 동기
현재 `employee` 테이블 스키마는 Hibernate가 `Employee`/`BaseEntity` 엔티티를 보고
`spring.jpa.hibernate.ddl-auto: update` 설정으로 자동 생성·변경한다. 이 방식은:
- 실제 실행되는 DDL(컬럼 타입, NOT NULL, 길이 등)을 코드로 직접 확인할 수 없다.
- 스키마 변경 이력이 남지 않고, 엔티티 코드 변경만으로 스키마가 암묵적으로 바뀐다.
- 운영 환경에서 `ddl-auto: update`는 일반적으로 권장되지 않는 방식이다.

명시적인 `schema.sql` DDL 파일로 전환해 스키마를 코드로 직접 검토·관리하고자 한다.

## 요구사항
- `src/main/resources/schema.sql`에 `employee` 테이블 `CREATE TABLE` DDL을 작성한다.
  컬럼 구성은 기존 확정 ERD(`docs/specs/employee-management-spec.md`)를 그대로 따른다:
  `id`(PK, 자동 증가), `name`, `department`, `position`, `email`, `phone`, `hire_date`,
  `status`, `created_at`, `updated_at`.
- 애플리케이션 시작 시 Spring Boot가 `schema.sql`을 실행하도록 설정한다(`spring.sql.init.mode:
  always`). DDL은 `CREATE TABLE IF NOT EXISTS`로 작성해 테이블이 이미 있으면 아무 변경도
  가하지 않고, 없을 때만 생성한다. 기존 데이터는 그대로 유지된다.
- Hibernate 자동 스키마 생성(`ddl-auto: update`)은 끄고, 엔티티와 DDL 간 정합성만 검증하는
  모드(`validate`)로 전환한다.
- 기존 엔티티(`Employee`, `BaseEntity`)의 컬럼 매핑(`@Column` 이름·nullable 등)은 그대로
  두되, `schema.sql`의 DDL과 반드시 일치해야 한다(하나라도 다르면 `validate` 모드에서
  기동 실패).
- 로컬 개발 DB(PostgreSQL, `employee_management`)에서 재기동 시 테이블이 DDL대로 재생성/
  검증되는지 확인한다.

## 수용 기준
- [ ] `src/main/resources/schema.sql`에 `employee` 테이블 DDL이 존재한다.
- [ ] `application.yaml`의 `ddl-auto`가 `update`가 아닌 `validate`로 바뀌었다.
- [ ] 로컬에서 `./gradlew.bat bootRun` 실행 시 (테이블이 없으면) `schema.sql`의 DDL로
      테이블이 생성되고, (이미 있으면) 기존 테이블/데이터가 그대로 유지된 채 Hibernate가
      엔티티-DDL 불일치 없이 정상 기동한다.
- [ ] 기존 기능(목록/등록/상세/수정/상태변경)이 스키마 전환 후에도 동일하게 동작한다.
- [ ] DDL의 컬럼 구성이 확정 ERD와 정확히 일치한다.

## 범위 외 항목
- Flyway/Liquibase 등 버전 관리형 마이그레이션 도구 도입 (이번엔 단순 `schema.sql` 방식만).
- 기존 로컬 DB에 이미 쌓인 데이터의 마이그레이션 (테이블/데이터는 그대로 유지, 별도 이관 작업 없음).
- ERD 필드 자체의 변경(컬럼 추가/삭제, 유니크 제약 등) — 기존 확정 ERD를 그대로 따른다.
- 운영(production) 환경 구성.

## 미해결 질문
남은 세부사항 없음 — 아래 "확정된 항목" 참고.

### 확정된 항목 (기록용)
- 기존 로컬 DB의 `employee` 테이블/데이터는 유지한다. DDL은 `DROP TABLE` 없이
  `CREATE TABLE IF NOT EXISTS employee (...)` 형태로 작성해 기존 테이블을 건드리지 않는다.
- `schema.sql`은 테이블이 없을 때만 생성되도록 한다. Spring Boot의 `spring.sql.init.mode`는
  `always`로 설정하되(비-임베디드 DB인 PostgreSQL에서 `schema.sql`이 실행되려면 필요),
  DDL 자체를 `CREATE TABLE IF NOT EXISTS`로 작성해 테이블이 이미 있으면 아무 동작도 하지
  않도록 멱등하게 만든다.
