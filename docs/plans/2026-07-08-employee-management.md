# 직원관리 시스템 — 구현 계획

Spec: `docs/specs/employee-management-spec.md`

## 현재 상태 확인
- 이 프로젝트는 git을 쓰지 않는 로컬 과제다 — 브랜치·커밋·PR 단계는 없다. 파일을 바로
  작업 디렉터리에 작성한다.
- 코드베이스는 Spring Initializr 스켈레톤뿐이며(`EmployeeManagementApplication.java`만 존재),
  `application.yaml`에는 PostgreSQL datasource가 아직 없다.

## 접근 방식
- 전통적인 Spring MVC + Thymeleaf 애플리케이션으로 구현한다 (확정). JSON REST API는
  만들지 않는다 — HTML 폼과 Spring MVC 컨트롤러만 사용한다. 스펙의 "API 스펙 개요"는
  스택 무관 참조용 REST 표현일 뿐이며, 아래처럼 폼 기반 MVC 라우트로 매핑한다 (확정):

  | 스펙 개념 | 실제 라우트 |
  |---|---|
  | GET /employees (목록) | `GET /employees` |
  | POST /employees (등록) | `GET /employees/new` (폼) + `POST /employees` (제출) |
  | GET /employees/{id} (상세) | `GET /employees/{id}` |
  | PATCH /employees/{id} (수정) | `GET /employees/{id}/edit` (폼) + `POST /employees/{id}` (제출) |
  | PATCH /employees/{id}/status (상태 변경) | `POST /employees/{id}/status` |

- 계층 구조는 `spring-backend` 스킬 표준(controller/service/repository/dto/entity)을 따른다.
- 화면은 `thymeleaf-ui` 스킬 표준을 따른다 (공통 레이아웃 fragment + 상태 배지).
- 데이터 계층은 `jpa-postgres` 스킬 표준을 따른다 (공통 감사 컬럼, 네이밍, 검증/제약 이중화).
- 패키지 루트: `com.example.employeemanagement.employee` (+ 공통 코드는 `com.example.employeemanagement.common`).

## 단계

1. **로컬 PostgreSQL 연결 설정**
   `application.yaml`에 datasource를 추가한다 (확정된 접속 정보):
   - `url: jdbc:postgresql://localhost:5432/employee_management`
   - `username: postgres`
   - `password: postgres`

   나중에 값이 바뀔 수 있다고 하셨으므로, 값은 직접 박아넣지 않고
   `${DB_HOST:localhost}`, `${DB_PORT:5432}`, `${DB_NAME:employee_management}`,
   `${DB_USERNAME:postgres}`, `${DB_PASSWORD:postgres}` 형태의 플레이스홀더 + 기본값으로
   설정한다 — 환경변수를 안 주면 위 기본값 그대로 동작하고, 바뀌면 환경변수만 덮어쓰면 된다.
   `spring.jpa.hibernate.ddl-auto`와 JPA Auditing 관련 설정도 함께 추가한다.
   파일: `src/main/resources/application.yaml`

2. **공통 감사 베이스**
   `@MappedSuperclass` 기반 `BaseEntity`(createdAt/updatedAt, `@CreatedDate`/`@LastModifiedDate`)와
   `@EnableJpaAuditing` 설정 클래스를 추가한다.
   파일: `.../common/BaseEntity.java`, `.../config/JpaAuditingConfig.java`

3. **도메인 모델**
   `EmployeeStatus` enum(`ACTIVE`/`ON_LEAVE`/`RESIGNED`), `Employee` 엔티티(스펙의 필드 그대로:
   name/department/position/email/phone/hireDate/status), `EmployeeRepository`.
   파일: `.../employee/EmployeeStatus.java`, `.../employee/Employee.java`, `.../employee/EmployeeRepository.java`

4. **DTO**
   등록/수정 겸용 폼 커맨드 객체 `EmployeeForm`(Bean Validation: `@NotBlank`, `@Email`,
   전화번호는 `@Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}")`로 `010-1234-5678` 형식 고정,
   나머지 필드는 `@NotBlank`/`@NotNull`).
   파일: `.../employee/dto/EmployeeForm.java`

5. **서비스**
   `EmployeeService`: 목록 조회, 등록, 상세 조회, 수정, 상태 변경(전이 규칙
   `ACTIVE ⇄ ON_LEAVE → RESIGNED` 검증 포함, 잘못된 전이는 예외 처리).
   파일: `.../employee/EmployeeService.java`

6. **컨트롤러**
   `EmployeeController` — 위 라우트 매핑표대로 구현. 검증 실패 시 `BindingResult`로 폼을
   그대로 되돌리고 에러 메시지를 표시한다.
   파일: `.../employee/EmployeeController.java`

7. **화면**
   공통 레이아웃 fragment, 목록(`list.html`, 상태 배지 포함), 등록/수정 폼(`form.html`),
   상세(`detail.html`, 상태 변경 액션 포함). 최소한의 스타일용 CSS 1개.
   파일: `src/main/resources/templates/employee/*.html`, `src/main/resources/static/css/*.css`

8. **예외 처리**
   존재하지 않는 id 접근, 잘못된 상태 전이 등에 대한 처리(간단한 에러 화면 또는
   플래시 메시지와 함께 리다이렉트).
   파일: `.../common/GlobalExceptionHandler.java` (필요 시)

9. **수동 검증**
   로컬에서 `./gradlew.bat bootRun`으로 실행해 브라우저로 F-01~F-04 전 항목을 확인하고,
   `docs/specs/employee-management-spec.md`의 체크리스트와 대조한다.

## 작업 순서 (git 없음 — 커밋 단위 대신 진행 순서)
1. 환경설정 + 공통 감사 베이스
2. 도메인 모델 + 리포지토리
3. 서비스 (상태 전이 로직 포함)
4. 컨트롤러 + DTO
5. 화면(Thymeleaf 템플릿)
6. 마무리 / 수동 검증 중 발견된 수정

## 확정된 항목 (기록용)
1. JSON REST API는 만들지 않는다 — HTML 폼 + Spring MVC 컨트롤러만 사용.
2. `phone` 형식: `010-1234-5678` (하이픈 포함, 자리수 고정).
3. 로컬 PostgreSQL 접속 정보: `localhost:5432` / DB `employee_management` / 계정
   `postgres`/`postgres` (환경변수로 나중에 덮어쓸 수 있게 기본값으로 반영).

## 리스크 / 열린 질문 (해결됨)
1. **`ddl-auto` 전략** — `update`로 확정 (로컬 개발 편의를 위해 엔티티 변경 시 스키마가
   자동으로 따라가도록 함).

이 계획은 사용자가 명시적으로 승인한 뒤에만 `executing-plans`로 넘어가 구현을 시작한다.
