
package com.todo.todoList.multitenancy.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.todo.todoList.repository",
        entityManagerFactoryRef = "todoEntityManagerFactory",
        transactionManagerRef = "todoTransactionManager"
)
public class TodoDatabaseConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties todoDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource todoDataSource(
            @Qualifier("todoDataSourceProperties")
            DataSourceProperties properties) {

        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean todoEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("todoDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("com.todo.todoList.model")
                .persistenceUnit("todo")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager todoTransactionManager(
            @Qualifier("todoEntityManagerFactory")
            LocalContainerEntityManagerFactoryBean entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory.getObject());
    }
}
