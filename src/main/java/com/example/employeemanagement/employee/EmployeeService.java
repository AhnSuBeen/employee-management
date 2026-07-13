package com.example.employeemanagement.employee;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.employeemanagement.employee.dto.EmployeeForm;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private static final Map<EmployeeStatus, EnumSet<EmployeeStatus>> ALLOWED_TRANSITIONS = Map.of(
            EmployeeStatus.ACTIVE, EnumSet.of(EmployeeStatus.ON_LEAVE, EmployeeStatus.RESIGNED),
            EmployeeStatus.ON_LEAVE, EnumSet.of(EmployeeStatus.ACTIVE, EmployeeStatus.RESIGNED),
            EmployeeStatus.RESIGNED, EnumSet.noneOf(EmployeeStatus.class));

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll(String name, String department, String position) {
        return employeeRepository.search(normalize(name), normalize(department), normalize(position));
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : "";
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public EnumSet<EmployeeStatus> getAllowedNextStatuses(EmployeeStatus status) {
        return ALLOWED_TRANSITIONS.get(status);
    }

    @Transactional
    public Employee create(EmployeeForm form) {
        Employee employee = new Employee();
        applyForm(employee, form);
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee update(Long id, EmployeeForm form) {
        Employee employee = findById(id);
        applyForm(employee, form);
        return employee;
    }

    @Transactional
    public Employee changeStatus(Long id, EmployeeStatus newStatus) {
        Employee employee = findById(id);
        EmployeeStatus currentStatus = employee.getStatus();
        if (!ALLOWED_TRANSITIONS.get(currentStatus).contains(newStatus)) {
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }
        employee.setStatus(newStatus);
        return employee;
    }

    private void applyForm(Employee employee, EmployeeForm form) {
        employee.setName(form.getName());
        employee.setDepartment(form.getDepartment());
        employee.setPosition(form.getPosition());
        employee.setEmail(form.getEmail());
        employee.setPhone(form.getPhone());
        employee.setHireDate(form.getHireDate());
    }
}
