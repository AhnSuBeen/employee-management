# 직원 화면 카드형 레이아웃 — 구현 계획

스펙: `docs/specs/employee-detail-card-layout.md`, `docs/specs/employee-list-form-card-layout.md`

> 이 문서는 원래 두 개의 계획서(`employee-detail-card-layout.md`,
> `employee-list-form-card-layout.md`)로 나뉘어 있던 것을 하나의 연속 작업으로 합친
> 것이다. 1단계에서 상세 페이지에 카드 컴포넌트를 만들고, 2단계에서 그 컴포넌트를
> 목록·폼 페이지로 확장했다.

## 접근 방식
공용 카드 컨테이너(`.card`) 하나를 CSS로 만들고, 상세→목록→폼 순서로 화면마다
기존 마크업(`dl`, `table`, `form`)을 그 컨테이너로 감싸거나 그 안에서 그리드로
재구성한다. 새 파일이나 의존성은 추가하지 않는다.

## 구현 단계

### 1단계 — 상세 페이지에 카드 도입
1. `templates/employee/detail.html`
   - `dl/dt/dd`를 `<div class="detail-card">` 안의 `<div class="detail-grid">`로 교체하고,
     각 필드를 `<div class="detail-field"><span class="detail-label">라벨</span><span
     class="detail-value">...</span></div>` 형태로 재구성한다.
   - 상태 필드는 `detail-value` 안에 기존 `statusBadge` 프래그먼트를 그대로 유지한다.
   - `✏️ 수정` → `수정`으로 이모지 제거.
   - 상태 변경 버튼 영역(`status-actions`)은 카드 하단에 배치하되 기존 폼 구조는 그대로 둔다.
2. `static/css/style.css`
   - `.detail-card` — 흰 배경, 그림자, 라운드 모서리, 여백.
   - `.detail-grid` — CSS grid로 라벨-값 2열 배치(`minmax` 사용).
   - `.detail-label`/`.detail-value` — 라벨은 회색 톤 작은 글자, 값은 본문 크기.
   - `.detail-card .status-actions` — 카드 하단 구분선 + 여백.

### 2단계 — 목록·폼으로 카드 확장
3. `static/css/style.css`
   - `.detail-card` 선택자를 `.card`로 이름을 바꾼다(내용 동일). `.detail-card > p`,
     `.detail-card .status-actions`도 `.card > p`, `.card .status-actions`로 함께 변경.
   - `table`의 `box-shadow`/`background-color`를 제거하고, `.card` 컨테이너가 배경·그림자를
     담당하도록 한다(이중 그림자 방지).
4. `templates/employee/detail.html`
   - `class="detail-card"` → `class="card"`로 변경(구조·내용 변경 없음).
5. `templates/employee/list.html`
   - `<table>...</table>` 전체를 `<div class="card">...</div>`로 감싼다.
6. `templates/employee/form.html`
   - `<form>...</form>` 전체를 `<div class="card">...</div>`로 감싼다.

### 회귀 확인 (두 단계 공통)
- 상세: 부서/직급/이메일/전화번호/입사일/상태 표시, 수정 링크·상태 변경 버튼 동작 확인.
- 목록: 이름 링크, 상태 배지, zebra 유지 확인.
- 폼: 등록/수정 정상 렌더링, 필수값 누락 시 에러 메시지 표시 확인.

## 대상 파일 및 모듈
- `src/main/resources/templates/employee/detail.html`
- `src/main/resources/templates/employee/list.html`
- `src/main/resources/templates/employee/form.html`
- `src/main/resources/static/css/style.css`

## 테스트 전략
순수 시각적 변경이므로 자동 테스트는 추가하지 않는다 — 브라우저 스크린샷으로 검증.

## 검증 방법
1. `./gradlew.bat bootRun`으로 로컬 기동.
2. 헤드리스 브라우저 스크린샷으로 상세/목록/폼(등록·수정) 페이지가 카드 스타일로
   일관되게 보이는지 확인.
3. 목록→상세 이동 링크, 폼 검증 오류 표시가 정상 동작하는지 확인.

## 브랜치명
`feature/employee-detail-card-layout` (1·2단계 모두 이 브랜치에서 이어서 진행됨)

## 리스크 / 미해결 질문
- `.detail-card` → `.card`로 이름을 바꾸면서 다른 곳에서 `.detail-card`를 참조하는 코드가
  없는지 확인 필요 — 확인 결과 `detail.html` 한 곳뿐이었다.
- 표 자체의 그림자를 제거하면 카드 밖에서 표만 단독으로 쓰이는 곳이 있을 경우 스타일이
  바뀔 수 있으나, `table`은 `list.html`에서만 쓰여 영향 없음.
- (1단계 시점) `feature/employee-schema-ddl` 브랜치가 아직 병합되지 않아 `main`에서 새
  브랜치로 독립 진행했고, 사용자가 진행 중이던 이모지 제거 작업(미커밋 상태)도 이 브랜치에
  함께 커밋됐다.
