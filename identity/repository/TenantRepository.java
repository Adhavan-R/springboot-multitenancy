
package com.todo.todoList.multitenancy.identity.repository;

import com.todo.todoList.multitenancy.identity.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByCompanyCode(String companyCode);

}
