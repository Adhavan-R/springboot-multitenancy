
package com.todo.todoList.multitenancy.identity.repository;

import com.todo.todoList.multitenancy.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndTenant_TenantId(
            String email,
            Long tenantId
    );

    boolean existsByEmailAndTenant_TenantId(
            String email,
            Long tenantId
    );

}
