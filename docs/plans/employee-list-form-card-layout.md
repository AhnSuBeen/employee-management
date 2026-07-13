# 목록 · 폼 페이지 카드 레이아웃 통일 — 구현 계획

스펙: `docs/specs/employee-list-form-card-layout.md`

## 접근 방식
상세 페이지에서 이미 만든 카드 컨테이너 스타일(`.detail-card`)을 `.card`로 일반화하고,
목록/폼 템플릿에서 기존 표·폼을 그 컨테이너로 감싼다. 상세 페이지 전용 그리드 스타일
(`.detail-grid` 등)은 이름을 바꾸지 않고 그대로 둔다. 새 파일·의존성 추가 없음.

## 구현 단계
1. `static/css/style.css`
   - `.detail-card` 선택자를 `.card`로 이름을 바꾼다(내용은 동일: 흰 배경, 그림자, 둥근
     모서리, 여백). `.detail-card > p`, `.detail-card .status-actions`도 `.card > p`,
     `.card .status-actions`로 함께 변경한다.
   - `table`의 `box-shadow`/`background-color`를 제거하고, 대신 `.card` 컨테이너가 배경과
     그림자를 담당하도록 한다(표가 카드 안에 들어갈 때 이중 그림자가 생기지 않도록).
   - `.card table`처럼 표가 카드 안에 있을 때의 여백을 자연스럽게 다듬는다(필요 시
     `margin-top: 0`).
2. `templates/employee/detail.html`
   - `class="detail-card"` → `class="card"`로 변경(스타일 이름 변경만 반영, 구조·내용
     변경 없음).
3. `templates/employee/list.html`
   - `<h1>직원 목록</h1>` 아래 `<table>...</table>` 전체를 `<div class="card">...</div>`로
     감싼다.
4. `templates/employee/form.html`
   - `<h1 ...>` 아래 `<form>...</form>` 전체를 `<div class="card">...</div>`로 감싼다.
5. 회귀 확인
   - 목록: 이름 링크, 상태 배지, 표 정렬(zebra) 유지 확인.
   - 폼: 등록/수정 각각 정상 렌더링, 필수값 누락 시 에러 메시지 표시 확인.
   - 상세: 클래스 이름 변경 후에도 기존 카드 모양이 그대로인지 확인.

## 대상 파일 및 모듈
- `src/main/resources/static/css/style.css` (수정)
- `src/main/resources/templates/employee/detail.html` (수정 — 클래스명만)
- `src/main/resources/templates/employee/list.html` (수정)
- `src/main/resources/templates/employee/form.html` (수정)

## 테스트 전략
- 순수 시각적 변경이므로 자동 테스트는 추가하지 않는다 — 브라우저 스크린샷으로 검증.

## 검증 방법
1. `./gradlew.bat bootRun`으로 로컬 기동.
2. 헤드리스 브라우저 스크린샷으로 목록/상세/폼(등록·수정) 페이지를 확인해 카드 스타일이
   세 화면에 일관되게 적용됐는지 확인.
3. 목록에서 상세로 이동하는 링크, 폼 검증 오류 표시가 정상 동작하는지 확인.

## 브랜치명
`feature/employee-detail-card-layout` (기존 브랜치에서 계속 진행 — 동일한 카드 디자인
작업의 연장이며 아직 병합되지 않았으므로 새 브랜치를 따로 만들지 않는다)

## 리스크 / 미해결 질문
- `.detail-card` → `.card`로 이름을 바꾸면서 혹시 다른 곳에서 `.detail-card`를 참조하는
  코드가 있는지 확인 필요(현재는 `detail.html` 한 곳뿐으로 확인됨).
- 표 자체의 그림자를 제거하면 카드 밖에서 표만 단독으로 쓰이는 곳이 있을 경우 스타일이
  바뀔 수 있으나, 현재 `table`은 `list.html`에서만 사용되므로 영향 없음.
