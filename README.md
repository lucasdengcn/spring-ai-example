# Spring AI Example

## Practices for Spring AI

This project demonstrates various implementation patterns and best practices for using Spring AI tools.

### Tools Implementation Patterns

#### Methods as Tools

Spring AI supports using methods as tools by annotating them with `@Tool`. Example from `DateTimeTools`:

```java
@Tool(name = "getCurrentDateTime", description = "Get the current date and time")
public String getCurrentDateTime() {
    return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
}
```

#### Tool Result Converter

Custom result converters can be implemented to control how tool results are formatted. Example from `CustomToolCallResultConverter`:

```java
@Tool(name = "getCustomer", 
      description = "Retrieve customer information", 
      resultConverter = CustomToolCallResultConverter.class)
public Customer getCustomer(String name, ToolContext context) {
    return new Customer(name, "example@email.com");
}
```

#### Tool Context

Spring AI provides a `ToolContext` parameter that can be injected into tool methods to access contextual information:

```java
public Customer getCustomerByEmail(String email, ToolContext context) {
    log.info("Context: {}", context);
    return new Customer("Demo", email);
}
```

#### Tool Parameters

Tool parameters can be annotated with `@ToolParam` to provide descriptions:

```java
@Tool(name = "setAlarm")
public void setAlarm(@ToolParam(description = "Time in ISO-8601 format") String time) {
    // Implementation
}
```

### Configuration

The project uses Spring Boot with the following key configurations:

- Ollama AI model integration
- Vector store with PGVector
- H2 database for development
- CORS configuration for web access
- MCP Server implementation
- MCP Client implementation
- SSE implementation

### SSE implementation

an implementation of SSE (Server-Sent Events) for real-time updates. This is achieved by using the `SseEmitter` class.

## Tecknology Stack

- Spring Boot
- Spring AI
- Ollama AI Model
- PGVector Vector Store
- H2 Database
- CORS Configuration
- SSE (Server-Sent Events) for real-time updates
