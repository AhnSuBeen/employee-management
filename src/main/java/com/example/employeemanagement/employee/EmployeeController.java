package com.example.employeemanagement.employee;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.employeemanagement.employee.dto.EmployeeForm;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("employees", employeeService.findAll());
        return "employee/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("employeeForm", new EmployeeForm());
        model.addAttribute("isEdit", false);
        return "employee/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("employeeForm") EmployeeForm form, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "employee/form";
        }
        Employee employee = employeeService.create(form);
        return "redirect:/employees/" + employee.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Employee employee = employeeService.findById(id);
        model.addAttribute("employee", employee);
        model.addAttribute("nextStatuses", employeeService.getAllowedNextStatuses(employee.getStatus()));
        return "employee/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Employee employee = employeeService.findById(id);
        EmployeeForm form = new EmployeeForm();
        form.setName(employee.getName());
        form.setDepartment(employee.getDepartment());
        form.setPosition(employee.getPosition());
        form.setEmail(employee.getEmail());
        form.setPhone(employee.getPhone());
        form.setHireDate(employee.getHireDate());
        model.addAttribute("employeeForm", form);
        model.addAttribute("employeeId", id);
        model.addAttribute("isEdit", true);
        return "employee/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("employeeForm") EmployeeForm form,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("employeeId", id);
            model.addAttribute("isEdit", true);
            return "employee/form";
        }
        employeeService.update(id, form);
        return "redirect:/employees/" + id;
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam EmployeeStatus status) {
        employeeService.changeStatus(id, status);
        return "redirect:/employees/" + id;
    }
}
