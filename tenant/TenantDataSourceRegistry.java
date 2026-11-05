package com.todo.todoList.multitenancy.tenant;

import com.todo.todoList.multitenancy.identity.entity.Tenant;
import com.todo.todoList.multitenancy.identity.repository.TenantRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Component
public class TenantDataSourceRegistry {

    private final TenantRepository tenantRepository;

    private final TenantDatabaseCredentialResolver credentialResolver;

    private final Map<Long, HikariDataSource> tenantDataSources =
            new ConcurrentHashMap<>();

    private static final Pattern DATABASE_NAME_PATTERN =
            Pattern.compile("[a-zA-Z0-9_]+");

    public TenantDataSourceRegistry(
            TenantRepository tenantRepository,
            TenantDatabaseCredentialResolver credentialResolver) {

        this.tenantRepository = tenantRepository;
        this.credentialResolver = credentialResolver;
    }

    // Load active tenant datasources during application startup
    @PostConstruct
    public void initialize() {

        System.out.println("Initializing tenant datasources...");

        for (Tenant tenant : tenantRepository.findAll()) {

            if ("ACTIVE".equalsIgnoreCase(tenant.getStatus())) {
                registerTenant(tenant);
            }
        }

        System.out.println(
                "Total tenant datasources registered: "
                        + tenantDataSources.size()
        );
    }

    // Create and register a datasource for a tenant
    public synchronized void registerTenant(Tenant tenant) {

        Long tenantId = tenant.getTenantId();

        if (tenantId == null) {
            throw new IllegalArgumentException(
                    "Tenant ID cannot be null"
            );
        }

        if (tenantDataSources.containsKey(tenantId)) {
            throw new IllegalStateException(
                    "Datasource already registered for tenant: " + tenantId
            );
        }

        // Only active tenants can receive datasources
        if (!"ACTIVE".equalsIgnoreCase(tenant.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot register an inactive tenant: " + tenantId
            );
        }

        // Validate tenant connection details
        validateTenantConfiguration(tenant);

        // Resolve the actual database password
        String password = credentialResolver.resolvePassword(
                tenant.getDatabasePasswordSecretRef()
        );

        // Construct JDBC URL using tenant-specific details
        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                tenant.getDatabaseHost(),
                tenant.getDatabasePort(),
                tenant.getDatabaseName()
        );

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(tenant.getDatabaseUsername());
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        config.setPoolName("Tenant-" + tenantId + "-Pool");

        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);

        // Fail registration if the database connection cannot be established
        config.setInitializationFailTimeout(10000);

        HikariDataSource dataSource = null;

        try {

            // Creating this pool also validates database connectivity
            dataSource = new HikariDataSource(config);

            // Register only after successful pool initialization
            tenantDataSources.put(tenantId, dataSource);

            System.out.println(
                    "Registered datasource for tenant: "
                            + tenantId
                            + " -> "
                            + tenant.getDatabaseName()
            );

        } catch (RuntimeException exception) {

            if (dataSource != null) {
                dataSource.close();
            }

            throw new IllegalStateException(
                    "Failed to register datasource for tenant: " + tenantId,
                    exception
            );
        }
    }

    // Validate tenant database configuration
    private void validateTenantConfiguration(Tenant tenant) {

        if (tenant.getDatabaseHost() == null ||
                tenant.getDatabaseHost().isBlank() ||
                tenant.getDatabaseHost().matches(".*[/?#@\\s].*")) {

            throw new IllegalArgumentException(
                    "Invalid database host for tenant: "
                            + tenant.getTenantId()
            );
        }

        if (tenant.getDatabasePort() == null ||
                tenant.getDatabasePort() < 1 ||
                tenant.getDatabasePort() > 65535) {

            throw new IllegalArgumentException(
                    "Invalid database port for tenant: "
                            + tenant.getTenantId()
            );
        }

        String databaseName = tenant.getDatabaseName();

        if (databaseName == null ||
                !DATABASE_NAME_PATTERN.matcher(databaseName).matches()) {

            throw new IllegalArgumentException(
                    "Invalid database name for tenant: "
                            + tenant.getTenantId()
            );
        }

        if (tenant.getDatabaseUsername() == null ||
                tenant.getDatabaseUsername().isBlank()) {

            throw new IllegalArgumentException(
                    "Database username is missing for tenant: "
                            + tenant.getTenantId()
            );
        }

        if (tenant.getDatabasePasswordSecretRef() == null ||
                tenant.getDatabasePasswordSecretRef().isBlank()) {

            throw new IllegalArgumentException(
                    "Database password reference is missing for tenant: "
                            + tenant.getTenantId()
            );
        }
    }

    // Retrieve datasource using tenant ID
    public DataSource getDataSource(Long tenantId) {

        HikariDataSource dataSource = tenantDataSources.get(tenantId);

        if (dataSource == null || dataSource.isClosed()) {

            throw new IllegalStateException(
                    "No active datasource registered for tenant: " + tenantId
            );
        }

        return dataSource;
    }

    // Close all tenant connection pools during application shutdown
    @PreDestroy
    public void closeAll() {

        tenantDataSources.values().forEach(HikariDataSource::close);

        tenantDataSources.clear();

        System.out.println("All tenant datasources closed.");
    }
}