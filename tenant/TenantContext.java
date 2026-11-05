
package com.todo.todoList.multitenancy.tenant;

public class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT =
            new ThreadLocal<>();

    // Store the tenant ID for the current request
    public static void setTenantId(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    // Retrieve the tenant ID for the current request
    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }

    // Remove tenant information after request completion
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
