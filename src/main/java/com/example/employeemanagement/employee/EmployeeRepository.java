package com.example.employeemanagement.employee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

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
}
