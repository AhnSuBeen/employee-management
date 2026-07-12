---
name: thymeleaf-ui
description: "Thymeleaf 템플릿으로 화면(목록/등록/상세/수정 페이지 등)을 추가하거나 수정할 때 사용. '~페이지 추가해줘', '화면 만들어줘', '폼 붙여줘' 같은 요청에 적용."
---

# Thymeleaf 화면 개발 표준 (thymeleaf-ui)

Spring MVC + Thymeleaf(서버 사이드 렌더링) 화면을 만들 때 따르는 표준.

## 템플릿 위치·구조
- 템플릿은 `src/main/resources/templates/<feature>/` 아래에 기능 단위로 둔다.
- 공통 레이아웃(헤더/네비게이션/푸터)은 Thymeleaf 레이아웃 조각(`th:fragment` / `th:replace`)으로 분리하고 각 페이지에서 재사용한다. 페이지마다 레이아웃을 복붙하지 않는다.
- 정적 리소스(CSS/JS)는 `src/main/resources/static/`에 둔다.

## 폼·바인딩
- 등록/수정 폼은 `th:object` + `th:field`로 커맨드(DTO) 객체에 바인딩한다.
- 서버 검증 실패 시 `BindingResult`를 컨트롤러에서 받아 같은 화면으로 되돌리고, `th:errors`로 필드별 에러 메시지를 표시한다.
- 상태값(재직/휴직/퇴직 등)처럼 고정된 선택지는 컨트롤러가 모델에 옵션 목록을 담아 넘기고, `th:each`로 렌더링한다.

## XSS 방어
- 동적 텍스트는 기본 `th:text`(자동 이스케이프)를 사용한다.
- `th:utext`(이스케이프 안 함)는 신뢰할 수 있는 정적 HTML 조각 외에는 사용하지 않는다. 사용자 입력을 `th:utext`로 렌더링하지 않는다.

## 상태 표시
- 상태값(재직/휴직/퇴직)은 배지 형태로 표시하고, 상태별 스타일 클래스를 CSS로 고정해 여러 화면에서 일관되게 사용한다.

## 하지 않는 것
- 프론트엔드 프레임워크(React/Vue 등) 임의 도입 — 이 프로젝트는 Thymeleaf SSR을 사용한다.
- 사용자 입력을 이스케이프 없이 렌더링
- 스펙에 없는 화면·입력 필드 임의 추가

## 관련
- 백엔드: `spring-backend`
- 데이터: `jpa-postgres`