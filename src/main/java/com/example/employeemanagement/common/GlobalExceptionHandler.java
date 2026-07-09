package com.example.employeemanagement.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.example.employeemanagement.employee.EmployeeNotFoundException;
import com.example.employeemanagement.employee.InvalidStatusTransitionException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ModelAndView handleNotFound(EmployeeNotFoundException ex) {
        return buildErrorView(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ModelAndView handleInvalidStatusTransition(InvalidStatusTransitionException ex) {
        return buildErrorView(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ModelAndView buildErrorView(HttpStatus status, String message) {
        ModelAndView modelAndView = new ModelAndView("error/error");
        modelAndView.addObject("message", message);
        modelAndView.setStatus(status);
        return modelAndView;
    }
}
