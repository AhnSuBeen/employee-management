# 직원 목록 부서별 그룹핑 — 구현 계획

스펙: `docs/specs/employee-list-department-grouping.md`

## 접근 방식
정렬은 리포지토리 단에서 파생 쿼리로 처리하고, 그룹 헤더 행은 뷰(`list.html`)에서
Thymeleaf `th:block` + `iterStat`으로 "이전 행과 부서가 다르면 헤더 행을 먼저 그린다"
방식으로 구현한다. 새 컨트롤러 경로나 파라미터는 추가하지 않는다 — `/employees` 응답이
항상 부서순으로 정렬되어 내려간다.

## 구현 단계
1. `EmployeeRepository`
   - `List<Employee> findAllByOrderByDepartmentAscNameAsc();` 파생 쿼리 메서드 추가.
2. `EmployeeService`
   - `findAll()`이 `employeeRepository.findAll()` 대신 위 파생 쿼리를 호출하도록 변경.
3. `templates/employee/list.html`
   - `tbody` 안에서 `th:each="employee, iterStat : ${employees}"`를 `<tr>`가 아닌
     `<th:block>`에 걸고, 그 안에 두 개의 형제 `<tr>`를 둔다.
     - 그룹 헤더 행: `th:if="${iterStat.index == 0 or employees[iterStat.index - 1].department != employee.department}"`
       조건일 때만 렌더링, `colspan`으로 전체 열을 덮고 부서명을 표시.
     - 데이터 행: 기존 내용 그대로 유지하되, zebra 교대를 위해
       `th:classappend="${employeeStat} % 2 == 1 ? 'row-alt' : ''"` 형태로 실제 데이터
       행 순서 기준의 클래스를 부여한다(그룹 헤더 행과 무관하게 데이터 행만 카운트).
4. `static/css/style.css`
   - 기존 `tbody tr:nth-child(even)` 규칙을 제거하고 `.row-alt` 클래스 기반 규칙으로
     대체한다.
   - `.dept-group-row` (또는 유사 이름) 스타일 추가 — 굵은 글씨, 옅은 배경(예: 카드
     배경보다 한 단계 진한 회색조), 데이터 행과 구분되는 여백.
5. 회귀 확인
   - 목록이 부서순으로 그룹핑되어 표시되는지 확인.
   - zebra 줄무늬가 그룹 헤더 행을 건너뛰고 데이터 행만 기준으로 올바르게 교대되는지 확인.
   - 상세 링크·상태 배지 등 기존 목록 기능, 등록/수정 후 목록 재조회가 정상 동작하는지
     확인.

## 대상 파일 및 모듈
- `src/main/java/.../employee/EmployeeRepository.java` (수정 — 파생 쿼리 추가)
- `src/main/java/.../employee/EmployeeService.java` (수정 — `findAll()` 구현 교체)
- `src/main/resources/templates/employee/list.html` (수정)
- `src/main/resources/static/css/style.css` (수정)

## 테스트 전략
- 자동 테스트는 추가하지 않는다(리포지토리 파생 쿼리는 Spring Data가 보장하는 기능이고,
  나머지는 뷰 변경) — 브라우저로 그룹핑·정렬·zebra를 직접 확인한다.

## 검증 방법
1. `./gradlew.bat bootRun`으로 로컬 기동.
2. 헤드리스 브라우저 스크린샷으로 목록 페이지를 확인해 부서별 그룹 헤더, 정렬, zebra
   교대가 의도대로 보이는지 확인.
3. 상세 페이지 이동, 상태 배지 표시가 정상인지 확인.
4. 신규 등록 후 목록에서 해당 부서 그룹에 올바르게 들어가는지 확인.

## 브랜치명
`feature/employee-list-department-grouping` — `feature/employee-detail-card-layout`의
현재 tip에서 분기한다(목록 페이지의 카드 래핑이 아직 `main`에 병합되지 않았으므로, 그
위에서 이어서 작업해야 카드 레이아웃이 유지된다).

## 리스크 / 미해결 질문
- 파생 쿼리 정렬 기준(부서명 오름차순)은 한글 로케일 정렬 규칙을 그대로 따른다(DB
  기본 collation) — 특별한 정렬 순서 요구사항은 없으므로 기본값을 사용한다.
- 그룹 헤더 행과 zebra 교대를 분리하는 로직이 Thymeleaf 표현식으로 다소 복잡해질 수
  있다 — 필요하면 컨트롤러에서 미리 "그룹 시작 여부"를 계산한 뷰 모델을 내려주는 방식으로
  단순화할 수 있으나, 우선 뷰 표현식만으로 구현해보고 복잡도가 과하면 사용자에게 알린다.
