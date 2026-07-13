# 직원 수정 폼 입사일(hireDate) 표시 오류 수정 — 구현 계획

스펙: `docs/specs/employee-form-hiredate-fix.md`

## 접근 방식
`EmployeeForm.hireDate` 필드에 `@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)`를
추가해, Spring의 포맷팅 변환 서비스가 이 필드를 `yyyy-MM-dd`로 바인딩/렌더링하도록
한다. 컨트롤러·서비스·템플릿은 변경하지 않는다(순수 DTO 애노테이션 추가).

## 구현 단계
1. `EmployeeForm.java`
   - `import org.springframework.format.annotation.DateTimeFormat;` 추가.
   - `hireDate` 필드에 `@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)` 추가.
2. 회귀 확인
   - 수정 폼(`/employees/{id}/edit`)에서 `입사일` 값이 채워져 보이는지 확인.
   - 등록 폼에서 날짜 선택 후 제출 → 정상 저장 확인.
   - 수정 폼에서 날짜 변경 후 제출 → 정상 반영 확인.
   - 입사일 비운 채 제출 → 기존과 동일한 필수값 에러 메시지 확인.

## 대상 파일 및 모듈
- `src/main/java/.../employee/dto/EmployeeForm.java` (수정)

## 테스트 전략
- 자동 테스트는 추가하지 않는다(단일 애노테이션 추가) — 브라우저로 직접 확인한다.

## 검증 방법
1. `./gradlew.bat bootRun`으로 로컬 기동.
2. 헤드리스 브라우저 스크린샷으로 수정 폼의 입사일 값이 채워져 보이는지 확인.
3. 등록/수정 폼 제출을 실제로 실행해 저장 결과(DB 값)를 확인.
4. 입사일 누락 제출로 검증 메시지 회귀 확인.

## 브랜치명
`feature/employee-form-hiredate-fix` — `feature/employee-form-grid-layout`의 현재
tip에서 분기한다(폼 그리드 레이아웃이 아직 `main`에 병합되지 않았으므로 그 위에서
이어서 작업).

## 리스크 / 미해결 질문
- 없음. 애노테이션 추가만으로 해결되는 잘 알려진 Spring MVC 패턴이다.
