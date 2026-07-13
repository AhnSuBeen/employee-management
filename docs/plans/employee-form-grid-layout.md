# 직원 등록/수정 폼 2단 그리드 레이아웃 — 구현 계획

스펙: `docs/specs/employee-form-grid-layout.md`

## 접근 방식
`form.html`의 `.form-row` 6개를 감싸는 `.form-grid` 컨테이너를 추가해 CSS grid 2열로
배치하고, 저장 버튼은 그리드 밖(아래)에 그대로 둔다. 입력창 스타일은 `style.css`의
`input[type="text"]`/`input[type="date"]`에 공통 규칙을 추가해 개선한다. 폼 바인딩
(`th:field`)과 검증(`th:errors`) 로직은 손대지 않는다.

## 구현 단계
1. `templates/employee/form.html`
   - 6개 `.form-row`를 `<div class="form-grid">...</div>`로 감싼다(폼 내용·바인딩·
     에러 표시는 그대로 유지).
   - `<button type="submit">저장</button>`은 `.form-grid` 바깥, 폼 안에 그대로 둔다.
2. `static/css/style.css`
   - `.form-grid` — `display: grid; grid-template-columns: repeat(2, 1fr); gap: 1.25rem 1.5rem;`
   - `input[type="text"], input[type="date"]` 공통 스타일 추가: 테두리 색 정리, 둥근
     모서리(`border-radius`), 패딩, `width: 100%`, `box-sizing: border-box`.
   - `input:focus` — 테두리 강조색 + `box-shadow`로 포커스 표시.
   - 기존 `.form-row`(margin-bottom 등)는 그리드 안에서도 그대로 재사용한다.
3. 회귀 확인
   - 등록 폼(`/employees/new`), 수정 폼(`/employees/{id}/edit`) 모두 2단 그리드로
     보이는지 확인.
   - 필수값 누락/형식 오류 제출 시 에러 메시지가 각 필드 아래 정상 표시되는지 확인.
   - 정상 등록·수정 흐름이 이전과 동일하게 동작하는지 확인.
   - 목록/상세 페이지가 영향받지 않았는지 확인(파일 미변경이지만 공용 CSS 규칙이라
     브라우저로 재확인).

## 대상 파일 및 모듈
- `src/main/resources/templates/employee/form.html` (수정)
- `src/main/resources/static/css/style.css` (수정)

## 테스트 전략
- 순수 시각적 변경이므로 자동 테스트는 추가하지 않는다 — 브라우저 스크린샷과 폼 제출로
  검증한다.

## 검증 방법
1. `./gradlew.bat bootRun`으로 로컬 기동.
2. 헤드리스 브라우저 스크린샷으로 등록/수정 폼의 2단 그리드, 입력창 스타일을 확인.
3. `curl`로 잘못된 값 제출 → 에러 메시지 정상 표시 확인.
4. 목록/상세 페이지 스크린샷으로 회귀 없는지 확인.

## 브랜치명
`feature/employee-form-grid-layout` — `feature/employee-list-search-filter`의 현재
tip에서 분기한다(목록 카드/검색 작업이 아직 `main`에 병합되지 않았으므로 그 위에서
이어서 작업).

## 리스크 / 미해결 질문
- 없음. 필드 6개(짝수)라 2열 그리드에서 마지막 줄까지 정확히 채워진다.
