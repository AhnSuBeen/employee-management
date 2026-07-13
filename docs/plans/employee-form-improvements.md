# 직원 등록/수정 폼 개선 (그리드 레이아웃 + 입사일 버그 수정) — 구현 계획

스펙: `docs/specs/employee-form-grid-layout.md`, `docs/specs/employee-form-hiredate-fix.md`

> 이 문서는 원래 두 개의 계획서(`employee-form-grid-layout.md`,
> `employee-form-hiredate-fix.md`)로 나뉘어 있던 것을 하나의 연속 작업으로 합친 것이다.
> 1단계에서 폼을 2단 그리드로 재배치했고, 그 과정의 회귀 검증 중 발견한 입사일 표시
> 버그를 2단계에서 별도로 수정했다.

## 접근 방식
1단계는 `form.html`/`style.css`만 건드리는 순수 레이아웃 변경이고, 2단계는
`EmployeeForm.java`에 날짜 포맷 애노테이션 하나를 추가하는 순수 버그 수정이다. 폼
바인딩(`th:field`)과 검증(`th:errors`) 로직은 두 단계 모두 손대지 않는다.

## 구현 단계

### 1단계 — 2단 그리드 레이아웃
1. `templates/employee/form.html`
   - 6개 `.form-row`(이름/부서/직급/이메일/전화번호/입사일)를 `<div class="form-grid">
     ...</div>`로 감싼다(내용·바인딩·에러 표시는 그대로 유지).
   - `<button type="submit">저장</button>`은 `.form-grid` 바깥에 그대로 둔다.
2. `static/css/style.css`
   - `.form-grid` — `display: grid; grid-template-columns: repeat(2, 1fr); gap: 1.25rem 1.5rem;`
   - `input[type="text"], input[type="date"]` 공통 스타일: 테두리 정리, 둥근 모서리,
     `width: 100%`, `box-sizing: border-box`.
   - `input:focus` — 테두리 강조색 + `box-shadow`.

### 2단계 — 입사일(hireDate) 표시 버그 수정
1단계 회귀 검증 중, 수정 폼(`/employees/{id}/edit`)에서 `입사일` 값이 항상 빈 칸으로
보이는 기존 버그를 발견했다. 원인: `EmployeeForm.hireDate`에 날짜 포맷 지정이 없어
Thymeleaf가 로케일 포맷(예: `26. 1. 1.`)으로 렌더링하는데, `<input type="date">`는
`yyyy-MM-dd`(ISO) 형식만 인식하므로 빈 칸으로 보였다.

3. `EmployeeForm.java`
   - `import org.springframework.format.annotation.DateTimeFormat;` 추가.
   - `hireDate` 필드에 `@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)` 추가.

### 회귀 확인 (두 단계 공통)
- 등록 폼(`/employees/new`), 수정 폼(`/employees/{id}/edit`) 모두 2단 그리드로 보이는지.
- 수정 폼에서 `입사일` 값이 `yyyy-MM-dd`로 채워져 보이는지.
- 필수값 누락/형식 오류 제출 시 에러 메시지가 정상 표시되는지.
- 정상 등록·수정 흐름이 이전과 동일하게 동작하는지.
- 목록/상세 페이지에 영향이 없는지.

## 대상 파일 및 모듈
- `src/main/resources/templates/employee/form.html`
- `src/main/resources/static/css/style.css`
- `src/main/java/.../employee/dto/EmployeeForm.java`

## 테스트 전략
자동 테스트는 추가하지 않는다 — 브라우저 스크린샷과 실제 폼 제출로 검증한다.

## 검증 방법
1. `./gradlew.bat bootRun`으로 로컬 기동.
2. 헤드리스 브라우저 스크린샷으로 등록/수정 폼의 2단 그리드, 입력창 스타일, 입사일
   표시를 확인.
3. `curl`로 잘못된 값 제출 → 에러 메시지 확인, 정상 값 제출 → 저장 결과(DB 값) 확인.
4. 목록/상세 페이지 스크린샷으로 회귀 없는지 확인.

## 브랜치명
- 1단계: `feature/employee-form-grid-layout` (`employee-list-search-filter` tip에서 분기)
- 2단계: `feature/employee-form-hiredate-fix` (1단계 브랜치 tip에서 이어서 분기)

## 리스크 / 미해결 질문
- 없음. 필드 6개(짝수)라 2열 그리드가 마지막 줄까지 정확히 채워지고, 날짜 포맷
  애노테이션 추가는 잘 알려진 Spring MVC 패턴이다.
