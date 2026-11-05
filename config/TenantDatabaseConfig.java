package com.todo.todoList.multitenancy.config;

import com.todo.todoList.multitenancy.tenant.TenantDataSourceRegistry;
import com.todo.todoList.multitenancy.tenant.TenantRoutingDataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.todo.todoList.multitenancy.employee.repository",
        entityManagerFactoryRef = "tenantEntityManagerFactory",
        transactionManagerRef = "tenantTransactionManager"
)
public class TenantDatabaseConfig {

    // Dynamic tenant routing datasource
    @Bean
    public DataSource tenantRoutingDataSource(
            TenantDataSourceRegistry tenantDataSourceRegistry) {

        return new TenantRoutingDataSource(tenantDataSourceRegistry);
    }

    // Tenant EntityManagerFactory
    @Bean
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("tenantRoutingDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();

        // Do not inspect the tenant database during startup
        properties.put("hibernate.boot.allow_jdbc_metadata_access", false);

        // Disable automatic schema changes for tenant databases
        properties.put("hibernate.hbm2ddl.auto", "none");

        // Explicitly specify the database dialect
        //properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

        return builder
                .dataSource(dataSource)
                .packages("com.todo.todoList.multitenancy.employee.entity")
                .persistenceUnit("tenant")
                .properties(properties)
                .build();
    }

    // Tenant transaction manager
    @Bean
    public PlatformTransactionManager tenantTransactionManager(
            @Qualifier("tenantEntityManagerFactory")
            LocalContainerEntityManagerFactoryBean entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory.getObject());
    }
}