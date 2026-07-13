# 직원 상세 페이지 카드형 레이아웃 개선 — 구현 계획

스펙: `docs/specs/employee-detail-card-layout.md`

## 접근 방식
`templates/employee/detail.html`의 마크업을 `dl`에서 카드 컨테이너 + CSS 그리드 기반의
라벨-값 레이아웃으로 바꾸고, `static/css/style.css`에 해당 스타일을 추가한다. 새 파일이나
의존성은 추가하지 않는다. 목록/폼/레이아웃 템플릿은 건드리지 않아 사용자가 별도로
진행 중인 작업과 충돌하지 않게 한다.

## 구현 단계
1. `templates/employee/detail.html`
   - `dl/dt/dd`를 `<div class="detail-card">` 안의 `<div class="detail-grid">`로 교체하고,
     각 필드를 `<div class="detail-field"><span class="detail-label">라벨</span><span
     class="detail-value">...</span></div>` 형태로 재구성한다.
   - 상태 필드는 `detail-value` 안에 기존 `statusBadge` 프래그먼트를 그대로 유지한다.
   - `✏️ 수정` → `수정`으로 이모지 제거.
   - 상태 변경 버튼 영역(`status-actions`)은 카드 하단에 배치하되 기존 폼 구조는 그대로 둔다.
2. `static/css/style.css`
   - `.detail-card` — 흰 배경, 그림자, 라운드 모서리, 여백(기존 `table`/`.badge` 스타일과
     톤 일치).
   - `.detail-grid` — CSS grid로 라벨-값 2열 배치(좁은 화면에서도 깨지지 않게 `minmax` 사용).
   - `.detail-label` — 회색 톤, 작은 글자, 굵게.
   - `.detail-value` — 본문 글자 크기, 진한 색.
   - `.detail-card .status-actions` — 카드 하단 구분선 + 여백으로 시각적으로 분리.
3. 회귀 확인
   - 상세 페이지에서 부서/직급/이메일/전화번호/입사일/상태가 올바르게 표시되는지 확인.
   - 수정 링크, 상태 변경 버튼 클릭 흐름이 이전과 동일하게 동작하는지 확인.
   - 목록/폼 페이지가 이번 변경으로 영향받지 않았는지 확인(파일 미변경이므로 자동 보장되나
     브라우저에서 한 번 더 확인).

## 대상 파일 및 모듈
- `src/main/resources/templates/employee/detail.html` (수정)
- `src/main/resources/static/css/style.css` (수정 — 스타일 추가만, 기존 규칙 유지)

## 테스트 전략
- 별도 자동 테스트는 추가하지 않는다(순수 시각적 변경) — 브라우저 스크린샷으로 검증.

## 검증 방법
1. `./gradlew.bat bootRun`으로 로컬 기동.
2. 브라우저(헤드리스 스크린샷)로 상세 페이지(`/employees/{id}`) 확인 — 카드 레이아웃,
   상태 배지, 수정 버튼, 상태 변경 버튼 노출 확인.
3. 목록 페이지(`/employees`)와 폼 페이지(`/employees/{id}/edit`)를 열어 이번 변경으로
   영향받지 않았는지 확인.

## 브랜치명
`feature/employee-detail-card-layout`

## 리스크 / 미해결 질문
- 기존 `feature/employee-schema-ddl` 브랜치가 아직 병합되지 않았다 — 이번 작업은 `main`에서
  새 브랜치를 분기해 독립적으로 진행한다(스키마 변경과 무관한 순수 UI 작업이므로 별도
  브랜치가 적절).
- 사용자가 `list.html`/`form.html`/`layout.html`에서 진행 중인 이모지 제거 작업은 이번
  브랜치와 무관하게 현재 작업 트리에 그대로 남아 있다 — 새 브랜치 생성 시 이 미커밋
  변경사항을 어떻게 다룰지 확인이 필요하다(예: 그대로 들고 가서 함께 커밋할지, 별도로
  둘지).
