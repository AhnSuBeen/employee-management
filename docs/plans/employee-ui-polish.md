# 직원관리 UI 개선 — implementation plan

Spec: `docs/specs/employee-ui-polish.md`

## Approach
CSS(`static/css/style.css`)와 기존 템플릿(`templates/fragments/layout.html`,
`templates/employee/*.html`)만 수정한다. 새 파일이나 의존성 추가는 없다.

## Steps
1. `static/css/style.css` — 네이비 헤더, 파란 계열 버튼, zebra 테이블 행 배경 스타일 추가.
   기존 배지 색상(`badge-active`/`badge-on-leave`/`badge-resigned`)은 유지한다.
2. `templates/fragments/layout.html` — `nav` 조각에 네이비 배경 클래스를 적용한다.
3. `templates/employee/list.html` — 테이블 헤더 라벨에 이모지 추가, 상태 배지에
   이모지+텍스트 함께 표시, `tbody` 행에 zebra 클래스 적용.
4. `templates/employee/detail.html` — `dt` 라벨에 이모지 추가, 상태 배지에 이모지+텍스트,
   버튼(수정/상태 변경)에 강조 버튼 클래스 적용.
5. `templates/employee/form.html` — `label`에 이모지 추가, 저장 버튼에 강조 버튼 클래스 적용.
6. 회귀 확인 — 이전에 검증했던 HTTP 시나리오(F-01 목록, F-02 등록/검증 실패, F-03 상세·수정,
   F-04 상태 전이 정상/비정상, 404)를 다시 실행해 동작이 그대로인지 확인한다.

## Risks / open questions
- 이모지 렌더링은 OS/폰트에 따라 다소 다를 수 있다 — 완벽한 크로스플랫폼 일치는 보장하지
  않으며, 이는 허용 범위로 간주한다.
