package com.todo.todoList.multitenancy.employee.controller;

import com.todo.todoList.multitenancy.employee.entity.Employee;
import com.todo.todoList.multitenancy.employee.repository.EmployeeRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mt")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/employees")
    public List<Employee> getEmployees() {

        return employeeRepository.findAll();
    }
}