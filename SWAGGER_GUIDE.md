# Swagger/OpenAPI Documentation Guide

## Overview

This project uses **SpringDoc OpenAPI 3** (the modern replacement for Swagger) to automatically generate interactive API documentation. SpringDoc is the recommended choice for Spring Boot 3.x applications as it provides better integration and performance compared to the legacy Swagger libraries.

## Accessing the Documentation

Once the application is running, you can access the API documentation at:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **OpenAPI YAML**: `http://localhost:8080/v3/api-docs.yaml`

## Features

- **Interactive API Testing**: Test endpoints directly from the browser
- **Automatic Schema Generation**: DTOs and models are automatically documented
- **Security Integration**: JWT authentication is properly documented
- **Multiple Environments**: Configured for both development and production

## Adding Documentation to Controllers

Here's how to document your API endpoints:

### 1. Controller Level Documentation

```java
@RestController
@RequestMapping("/api/example")
@Tag(name = "Example API", description = "Operations related to examples")
public class ExampleController {
    // controller methods
}
```

### 2. Method Level Documentation

```java
@Operation(
    summary = "Create a new example",
    description = "Creates a new example with the provided data"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Example created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid input data"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
@PostMapping
public ResponseEntity<ExampleDTO> createExample(
    @Parameter(description = "Example data to create", required = true)
    @RequestBody @Valid ExampleDTO exampleDTO) {
    // method implementation
}
```

### 3. Parameter Documentation

```java
@GetMapping("/{id}")
public ResponseEntity<ExampleDTO> getExample(
    @Parameter(description = "ID of the example to retrieve", example = "123")
    @PathVariable Long id) {
    // method implementation
}
```

## Configuration

The OpenAPI configuration is located in `OpenApiConfig.java` and includes:

- API title, description, and version
- Contact information
- License details
- Server configurations for different environments

## Security Configuration

The following endpoints are publicly accessible for documentation:

- `/swagger-ui/**`
- `/v3/api-docs/**`
- `/swagger-ui.html`

## Best Practices

1. **Use meaningful descriptions**: Provide clear, concise descriptions for all endpoints
2. **Include examples**: Add example values for parameters and request bodies
3. **Document all response codes**: Include all possible HTTP status codes
4. **Group related endpoints**: Use `@Tag` to organize endpoints logically
5. **Document security requirements**: Use `@SecurityRequirement` for protected endpoints

## Example: Complete Controller Documentation

```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Operations for managing users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a user by their unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User found",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
        @Parameter(description = "User ID", example = "1")
        @PathVariable Long id) {
        // implementation
    }
}
```

## Why SpringDoc over Legacy Swagger?

1. **Better Spring Boot 3 Support**: Native support for Spring Boot 3.x
2. **Automatic Configuration**: Minimal configuration required
3. **Performance**: Better performance and smaller footprint
4. **Active Development**: Actively maintained and updated
5. **Jakarta EE Support**: Full support for Jakarta EE annotations

## Additional Resources

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI 3.0 Specification](https://swagger.io/specification/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)