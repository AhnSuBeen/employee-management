package com.example.employeemanagement.employee;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(EmployeeStatus from, EmployeeStatus to) {
        super("허용되지 않는 상태 전이입니다: " + from + " -> " + to);
    }
}
