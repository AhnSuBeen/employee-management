---
name: spring-backend
description: "Spring MVC 컨트롤러, 서비스, DTO, 검증, 예외 처리 등 백엔드 API/로직을 작성하거나 수정할 때 사용. '~API 만들어줘', '컨트롤러 추가해줘', '서비스 로직 짜줘' 같은 요청에 적용."
---

# Spring 백엔드 개발 표준 (spring-backend)

이 프로젝트(Java 21 · Spring Boot 3.5 · Gradle)에서 백엔드 계층을 만들 때 따르는 표준.

## 계층 구조
- `controller` — HTTP 요청/응답과 입력 검증(`@Valid`)만 담당. 비즈니스 로직을 두지 않는다.
- `service` — 트랜잭션 경계(`@Transactional`)와 비즈니스 로직. 컨트롤러와 리포지토리 사이의 유일한 통로.
- `repository` — Spring Data JPA `interface XxxRepository extends JpaRepository<Entity, ID>`. 커스텀 쿼리가 필요하면 메서드 이름 규칙 또는 `@Query`를 사용한다.
- `dto` — 요청/응답 전용 객체. 엔티티를 컨트롤러 응답에 직접 노출하지 않는다.
- `entity` — JPA 엔티티. 상세 규칙은 `jpa-postgres` 스킬을 따른다.

패키지 예: `com.example.employeemanagement.<feature>.{controller,service,repository,dto,entity}` — 기존에 다른 구조가 이미 자리 잡혀 있다면 새 구조를 임의로 도입하지 말고 기존 것을 따른다.

## 요청 검증
- 요청 DTO 필드에 `spring-boot-starter-validation`의 애노테이션(`@NotBlank`, `@Email`, `@Pattern` 등)을 사용한다.
- 컨트롤러 메서드 파라미터에 `@Valid`를 붙인다.
- 검증 실패 시 전역 예외 처리기가 일관된 에러 응답을 반환하도록 한다(아래 참고).

## 응답 규약
- 성공 응답은 요청받은 스펙의 필드만 담은 DTO를 반환한다. 스펙에 없는 필드를 임의로 추가하지 않는다.
- 에러 응답은 프로젝트 전역에서 하나의 형식으로 통일한다(예: `{ "message": ..., "errors": [...] }`) — 새 컨트롤러마다 다른 에러 포맷을 만들지 않는다.
- `@RestControllerAdvice` 하나로 `MethodArgumentNotValidException`, 커스텀 비즈니스 예외, 404 등을 중앙에서 처리한다.

## Lombok 사용
- 엔티티/DTO에는 `@Getter`, 필요한 경우 `@Builder`, `@NoArgsConstructor`/`@AllArgsConstructor`를 사용한다.
- `@Data`는 엔티티에는 지양한다(`equals/hashCode`가 연관관계·프록시와 얽히는 문제를 피하기 위함). DTO에는 사용 가능.

## 하지 않는 것
- 컨트롤러에서 리포지토리를 직접 호출
- 스펙에 없는 엔드포인트·필드 임의 추가
- 서비스 계층 없이 컨트롤러에 비즈니스 로직을 직접 작성

## 관련
- 데이터 계층: `jpa-postgres`
- 화면: `thymeleaf-ui`
- 리뷰: `code-review-standard`