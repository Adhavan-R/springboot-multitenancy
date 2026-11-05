package com.todo.todoList.multitenancy.identity.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "company_code", nullable = false, unique = true, length = 50)
    private String companyCode;

    @Column(name = "database_name", nullable = false, length = 100)
    private String databaseName;

    @Column(name = "database_host", nullable = false, length = 255)
    private String databaseHost;

    @Column(name = "database_port", nullable = false)
    private Integer databasePort;

    @Column(name = "database_username", nullable = false, length = 100)
    private String databaseUsername;

    @Column(name = "database_password_secret_ref", nullable = false, length = 255)
    private String databasePasswordSecretRef;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // Getters and setters

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

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public void setDatabaseHost(String databaseHost) {
        this.databaseHost = databaseHost;
    }

    public Integer getDatabasePort() {
        return databasePort;
    }

    public void setDatabasePort(Integer databasePort) {
        this.databasePort = databasePort;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    public String getDatabasePasswordSecretRef() {
        return databasePasswordSecretRef;
    }

    public void setDatabasePasswordSecretRef(String databasePasswordSecretRef) {
        this.databasePasswordSecretRef = databasePasswordSecretRef;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}