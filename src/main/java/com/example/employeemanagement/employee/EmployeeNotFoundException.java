package com.example.employeemanagement.employee;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long id) {
        super("직원을 찾을 수 없습니다. id=" + id);
    }
}
