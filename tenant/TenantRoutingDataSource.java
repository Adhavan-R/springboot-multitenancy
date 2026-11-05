package com.todo.todoList.multitenancy.tenant;

import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TenantRoutingDataSource extends AbstractDataSource {

    private final TenantDataSourceRegistry tenantDataSourceRegistry;

    public TenantRoutingDataSource(
            TenantDataSourceRegistry tenantDataSourceRegistry) {

        this.tenantDataSourceRegistry = tenantDataSourceRegistry;
    }

    private DataSource determineTargetDataSource() {

        Long tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new IllegalStateException(
                    "Tenant ID is missing. Cannot determine database."
            );
        }

        return tenantDataSourceRegistry.getDataSource(tenantId);
    }

    @Override
    public Connection getConnection() throws SQLException {

        return determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(
            String username,
            String password) throws SQLException {

        return determineTargetDataSource()
                .getConnection(username, password);
    }
}