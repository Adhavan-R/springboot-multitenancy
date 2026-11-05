package com.todo.todoList.multitenancy.security.dto;

public class LoginResponse {

    private Long userId;
    private String name;
    private String email;
    private Long tenantId;
    private String companyName;
    private String companyCode;
    private String role;

    private String token;

    public LoginResponse() {
    }

    public LoginResponse(Long userId, String name, String email,
                         Long tenantId, String companyName,
                         String companyCode, String role, String token) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.tenantId = tenantId;
        this.companyName = companyName;
        this.companyCode = companyCode;
        this.role = role;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Generate getters and setters using IntelliJ

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}