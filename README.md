## Class Responsibilities – Multitenant Architecture

### Authentication and Security

- **AuthController:** Handles user login requests from the frontend and returns the authentication response.
- **AuthService:** Implements the login process. It identifies the tenant using the company code, retrieves the user from the central identity database, verifies the password, and requests JWT generation.
- **TenantRepository:** Fetches tenant information from the `tenants` table in the central identity database.
- **TenantRepository:** Fetches tenant information from the `tenants` table in the central identity database.
- **UserRepository:** Fetches user information using the email and tenant ID from the central identity database.
- **PasswordEncoder:** Verifies the entered password against the stored BCrypt password hash.
- **JwtService:** Generates JWT tokens containing user information such as user ID, tenant ID, company code, and role. It also validates tokens and extracts their claims.
- **JwtAuthenticationFilter:** Intercepts incoming authenticated API requests, extracts and validates the JWT, retrieves the user's information, sets the `TenantContext`, and establishes authentication in Spring Security's `SecurityContext`.
- **SecurityConfig:** Configures Spring Security, defines which endpoints require authentication, disables unwanted authentication mechanisms, and registers the JWT authentication filter.
- **LoginRequest:** DTO that carries the login credentials (company code, email, and password) from the frontend.
- **LoginResponse:** DTO that carries the generated JWT token and authenticated user's details back to the frontend.

### Tenant Identification and Database Routing

- **TenantContext:** Uses a `ThreadLocal` variable to temporarily store the tenant ID associated with the current request. This allows the application to identify which tenant's database should be accessed.
- **TenantRoutingDataSource:** Acts as the routing layer between the application and tenant databases. It reads the tenant ID from `TenantContext` and obtains the corresponding datasource from `TenantDataSourceRegistry`.
- **TenantDataSourceRegistry:** Maintains a collection of tenant-specific database connections (HikariCP datasources). It loads active tenants during application startup and provides the appropriate datasource whenever a tenant database is requested.
- **TenantDatabaseCredentialResolver:** Resolves database password references stored in the central identity database into actual database passwords. This keeps database passwords separate from tenant connection metadata.
- **TenantDatabaseConfig:** Configures the JPA persistence unit, entity manager, transaction manager, and repositories that operate on tenant databases. It uses `TenantRoutingDataSource` to dynamically route database operations.
- **IdentityDatabaseConfig:** Configures the separate JPA persistence unit, entity manager, transaction manager, and repositories for the central `company_identity` database.
- **TodoDatabaseConfig:** Configures the existing Todo application's independent database, keeping its repositories, entities, and transactions separate from the multitenant databases.

### Tenant Data and Business Logic

- **Tenant:** Entity representing a registered company/tenant. It stores company information, tenant ID, database connection details, password secret reference, and tenant status.
- **User:** Entity representing a user registered under a particular tenant. It stores user details, password hash, role, and the associated tenant ID.
- **Employee:** Entity representing employee-specific information stored inside each tenant's database.
- **EmployeeController:** Exposes employee-related REST API endpoints and handles incoming HTTP requests.
- **EmployeeService:** Contains the business logic for employee operations and coordinates with the employee repository.
- **EmployeeRepository:** Performs database operations on the `employees` table. Its queries are executed against the tenant database selected by the routing datasource.

**Overall:** The central identity database manages tenants and their users, while each tenant has an independent database containing its own business data. During login, the application identifies the tenant and generates a JWT containing the tenant ID. For subsequent API requests, the JWT authentication filter establishes the tenant context, and the routing datasource directs database operations to the corresponding tenant database. The existing Todo application continues to use its separate database.
