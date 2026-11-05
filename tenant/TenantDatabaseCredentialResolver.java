package com.todo.todoList.multitenancy.tenant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TenantDatabaseCredentialResolver {

    @Value("${tenant.db.secrets.local-mysql-shared}")
    private String localMysqlSharedPassword;

    public String resolvePassword(String secretReference) {

        if (secretReference == null || secretReference.isBlank()) {
            throw new IllegalArgumentException(
                    "Database password secret reference is missing"
            );
        }

        return switch (secretReference) {

            case "LOCAL_MYSQL_SHARED" -> {
                if (localMysqlSharedPassword == null ||
                        localMysqlSharedPassword.isBlank()) {

                    throw new IllegalStateException(
                            "Local MySQL password is not configured"
                    );
                }

                yield localMysqlSharedPassword;
            }

            default -> throw new IllegalArgumentException(
                    "Unknown database password secret reference: "
                            + secretReference
            );
        };
    }
}