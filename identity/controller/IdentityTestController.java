
package com.todo.todoList.multitenancy.identity.controller;

import com.todo.todoList.multitenancy.identity.entity.Tenant;
import com.todo.todoList.multitenancy.identity.repository.TenantRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mt/test")
public class IdentityTestController {

    private final TenantRepository tenantRepository;

    public IdentityTestController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @GetMapping("/identity")
    public List<Map<String, Object>> testIdentityDatabase() {

        return tenantRepository.findAll()
                .stream()
                .map(tenant -> Map.<String, Object>of(
                        "tenantId", tenant.getTenantId(),
                        "companyName", tenant.getCompanyName(),
                        "companyCode", tenant.getCompanyCode(),
                        "databaseName", tenant.getDatabaseName()
                ))
                .toList();
    }
}
