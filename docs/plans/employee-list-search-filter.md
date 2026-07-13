# 직원 목록 검색/필터 — 구현 계획

스펙: `docs/specs/employee-list-search-filter.md`

## 접근 방식
검색 조건(이름/부서/직급)은 `/employees` GET 쿼리 파라미터로 받는다. 리포지토리에
JPQL `@Query`로 "값이 없으면(null) 조건을 건너뛰고, 있으면 대소문자 무관 부분 일치"
쿼리를 하나 추가해 세 조건의 AND 결합과 기존 부서→이름 정렬을 동시에 처리한다.
컨트롤러는 빈 문자열을 `null`로 정규화해 서비스에 넘긴다. 뷰는 `list.html`을
사이드바(검색 폼) + 기존 카드(표) 2단 레이아웃으로 바꾸고, 검색값을 폼에 다시 채워
넣는다.

## 구현 단계
1. `EmployeeRepository`
   - JPQL 검색 쿼리 추가:
     ```java
     @Query("""
         SELECT e FROM Employee e
         WHERE (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
           AND (:department IS NULL OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%')))
           AND (:position IS NULL OR LOWER(e.position) LIKE LOWER(CONCAT('%', :position, '%')))
         ORDER BY e.department ASC, e.name ASC
         """)
     List<Employee> search(@Param("name") String name,
                            @Param("department") String department,
                            @Param("position") String position);
     ```
   - 기존 `findAllByOrderByDepartmentAscNameAsc()`는 검색 조건이 모두 비어 있을 때도
     `search(null, null, null)`로 동일하게 커버되므로 제거하고 `search`로 통합한다.
2. `EmployeeService`
   - `findAll()` 시그니처를 `findAll(String name, String department, String position)`로
     바꾸고, 각 인자를 `StringUtils.hasText(...) ? value.trim() : null`로 정규화한 뒤
     `employeeRepository.search(...)`를 호출한다.
3. `EmployeeController`
   - `list` 메서드에 `@RequestParam(required = false) String name`,
     `@RequestParam(required = false) String department`,
     `@RequestParam(required = false) String position`을 추가한다.
   - `employeeService.findAll(name, department, position)`을 호출하고, 모델에 검색값
     3개(`nameFilter`, `departmentFilter`, `positionFilter`)를 그대로 담아 폼에 값이
     유지되도록 한다.
4. `templates/employee/list.html`
   - `<h1>직원 목록</h1>` 아래를 `<div class="list-layout">`로 감싸고, 그 안에
     `<aside class="card filter-sidebar">`(검색 폼)과 `<div class="list-content">`
     (기존 카드+표)를 나란히 둔다.
   - 검색 폼: `method="get"`, `th:action="@{/employees}"`, 이름/부서/직급 입력 필드
     (`th:value="${nameFilter}"` 등으로 값 유지), 검색 버튼, 초기화 링크
     (`th:href="@{/employees}"`).
   - `${employees}`가 비어 있을 때 안내 문구("검색 결과가 없습니다" 등)를 표시한다
     (`th:if="${employees.isEmpty()}"`).
5. `static/css/style.css`
   - `.list-layout` — flex 컨테이너(사이드바 고정 너비 + 본문 가변 너비).
   - `.filter-sidebar` — 고정 너비(예: 220px), 기존 `.card`와 동일한 톤 유지.
   - 좁은 화면 대응은 이번 범위에서 다루지 않는다(스펙에 반응형 요구 없음).
6. 회귀 확인
   - 조건 없음 → 전체 목록(부서 그룹핑 그대로).
   - 이름만 입력 → 부분 일치 확인.
   - 부서+직급 동시 입력 → AND 결합 확인.
   - 결과 없는 조합 → 빈 상태 문구 확인.
   - 검색 후 폼에 입력값이 유지되는지 확인.
   - 상세/등록/수정/상태변경 등 기존 기능 회귀 확인.

## 대상 파일 및 모듈
- `src/main/java/.../employee/EmployeeRepository.java` (수정)
- `src/main/java/.../employee/EmployeeService.java` (수정 — `findAll` 시그니처 변경)
- `src/main/java/.../employee/EmployeeController.java` (수정 — 쿼리 파라미터 추가)
- `src/main/resources/templates/employee/list.html` (수정)
- `src/main/resources/static/css/style.css` (수정)

## 테스트 전략
- 자동 테스트는 추가하지 않는다(기존 프로젝트에 컨트롤러/서비스 테스트가 없음) — 브라우저
  및 직접 HTTP 요청(`curl`의 쿼리 파라미터)으로 검증한다.

## 검증 방법
1. `./gradlew.bat bootRun`으로 로컬 기동.
2. `curl "http://localhost:8080/employees?department=개발"` 등으로 필터링 결과 확인.
3. 헤드리스 브라우저 스크린샷으로 사이드바 레이아웃, 검색 전/후 화면, 빈 결과 화면을 확인.
4. 상세/등록/수정/상태변경 등 기존 흐름이 정상 동작하는지 확인.

## 브랜치명
`feature/employee-list-search-filter` — `feature/employee-list-department-grouping`의
현재 tip에서 분기한다(목록의 그룹핑/카드 레이아웃이 아직 `main`에 병합되지 않았으므로,
그 위에서 이어서 작업해야 이전 작업 결과가 유지된다).

## 리스크 / 미해결 질문
- 원래 스펙(`employee-management-spec.md`)이 F-05를 범위 제외로 명시했었다 — 이번 스펙이
  그 결정을 뒤집는 것이므로, 두 문서 간 불일치를 인지하고 있어야 한다(원본 스펙은 수정하지
  않고 새 스펙으로 갈음).
- LIKE 검색에 `department`/`position`처럼 값이 정해진 필드도 자유 텍스트 부분 일치로
  처리한다 — 오타나 공백에 민감할 수 있으나, 이번 범위에서는 별도 자동완성/고정 선택지
  없이 단순 텍스트 검색으로 처리하기로 확정했다.
