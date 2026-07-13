# 직원 목록 페이지 개선 (그룹핑 + 검색) — 구현 계획

스펙: `docs/specs/employee-list-department-grouping.md`, `docs/specs/employee-list-search-filter.md`

> 이 문서는 원래 두 개의 계획서(`employee-list-department-grouping.md`,
> `employee-list-search-filter.md`)로 나뉘어 있던 것을 하나의 연속 작업으로 합친 것이다.
> 1단계에서 목록을 부서별로 그룹핑·정렬하고, 2단계에서 그 위에 검색 사이드바를 추가했다.

## 접근 방식
정렬·필터링은 항상 리포지토리/서비스 단에서 처리하고(뷰에서 재정렬하지 않음), 그룹
헤더 행은 Thymeleaf `th:block`+`iterStat`으로, 검색은 GET 쿼리 파라미터 + JPQL
`@Query`로 구현한다. 새 컨트롤러 경로는 추가하지 않는다.

## 구현 단계

### 1단계 — 부서별 그룹핑
1. `EmployeeRepository`
   - `List<Employee> findAllByOrderByDepartmentAscNameAsc();` 파생 쿼리 메서드 추가.
2. `EmployeeService`
   - `findAll()`이 `employeeRepository.findAll()` 대신 위 파생 쿼리를 호출하도록 변경.
3. `templates/employee/list.html`
   - `tbody` 안에서 `th:each="employee, iterStat : ${employees}"`를 `<tr>`가 아닌
     `<th:block>`에 걸고, 그 안에 두 개의 형제 `<tr>`를 둔다.
     - 그룹 헤더 행: `th:if="${iterStat.index == 0 or employees[iterStat.index - 1].department != employee.department}"`,
       `colspan`으로 전체 열을 덮고 부서명 표시.
     - 데이터 행: `th:classappend`로 `iterStat.index` 기반 `row-alt` 클래스 부여(그룹
       헤더 행과 무관하게 데이터 행만 카운트).
4. `static/css/style.css`
   - 기존 `tbody tr:nth-child(even)` 규칙을 `.row-alt` 클래스 기반 규칙으로 대체.
   - `.dept-group-row` 스타일 추가(굵은 글씨, 옅은 배경).

### 2단계 — 검색 사이드바
5. `EmployeeRepository`
   - JPQL 검색 쿼리로 교체(1단계 파생 쿼리 대신 통합):
     ```java
     @Query("""
         SELECT e FROM Employee e
         WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))
           AND LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%'))
           AND LOWER(e.position) LIKE LOWER(CONCAT('%', :position, '%'))
         ORDER BY e.department ASC, e.name ASC
         """)
     List<Employee> search(@Param("name") String name,
                            @Param("department") String department,
                            @Param("position") String position);
     ```
   - 처음엔 `(:name IS NULL OR ...)` 형태로 빈 조건을 건너뛰려 했으나, 값이 없을 때 `null`
     파라미터를 넘기면 PostgreSQL이 `LOWER(?)`의 인자 타입을 추론하지 못해 500 에러가
     발생함을 구현 중 확인했다. 대신 빈 값은 `null`이 아니라 **빈 문자열(`""`)**로
     정규화하고 `IS NULL` 분기를 없앴다 — `LIKE '%%'`는 모든 문자열에 매치되므로
     "조건 없음"과 동일하게 동작한다.
6. `EmployeeService`
   - `findAll()` 시그니처를 `findAll(String name, String department, String position)`로
     바꾸고, 각 인자를 `StringUtils.hasText(...) ? value.trim() : ""`로 정규화한 뒤
     `employeeRepository.search(...)`를 호출한다.
7. `EmployeeController`
   - `list` 메서드에 `@RequestParam(required = false) String name/department/position`
     추가, 모델에 `nameFilter`/`departmentFilter`/`positionFilter`로 검색값을 담아 폼에
     값이 유지되도록 한다.
8. `templates/employee/list.html`
   - `<div class="list-layout">`로 감싸고, `<aside class="card filter-sidebar">`(검색
     폼: 이름/부서/직급 입력 + 검색 버튼 + 초기화 링크)와 `<div class="list-content">`
     (기존 카드+표)를 나란히 둔다.
   - `${employees}`가 비어 있을 때 "검색 결과가 없습니다" 문구 표시.
9. `static/css/style.css`
   - `.list-layout`(flex 컨테이너), `.filter-sidebar`(고정 너비), `.filter-actions`(검색/
     초기화 버튼 동일 너비).

## 대상 파일 및 모듈
- `src/main/java/.../employee/EmployeeRepository.java`
- `src/main/java/.../employee/EmployeeService.java`
- `src/main/java/.../employee/EmployeeController.java`
- `src/main/resources/templates/employee/list.html`
- `src/main/resources/static/css/style.css`

## 테스트 전략
자동 테스트는 추가하지 않는다 — 브라우저 및 `curl` 쿼리 파라미터로 검증한다.

## 검증 방법
1. `./gradlew.bat bootRun`으로 로컬 기동.
2. 부서별 그룹 헤더·정렬·zebra 교대를 스크린샷으로 확인.
3. `curl "http://localhost:8080/employees?department=개발"` 등으로 단일/복합 필터, AND
   결합, 빈 결과, 값 유지를 확인.
4. 상세/등록/수정/상태변경 등 기존 흐름 회귀 확인.

## 브랜치명
- 1단계: `feature/employee-list-department-grouping` (`employee-detail-card-layout` tip
  에서 분기)
- 2단계: `feature/employee-list-search-filter` (1단계 브랜치 tip에서 이어서 분기)

## 리스크 / 미해결 질문
- 부서명 정렬은 DB 기본 collation을 그대로 따른다(특별 요구사항 없음).
- 원래 스펙(`employee-management-spec.md`)이 검색·필터(F-05)를 범위 제외로 명시했었는데,
  2단계 스펙이 그 결정을 뒤집었다 — 원본 스펙 문서도 이에 맞춰 수정했다.
- LIKE 검색은 `department`/`position`도 자유 텍스트 부분 일치로 처리한다(고정 선택지·
  자동완성 없음, 확정된 범위).
