# Proposal Agent

## Overview

The Proposal Agent module implements the Model Context Protocol (MCP) client for making AI-powered proposals. It demonstrates how to integrate Spring AI with MCP client capabilities.

## Features

- MCP Client implementation
- WebFlux SSE Client support
- AI-powered proposal generation
- Real-time updates via SSE

## Dependencies

```gradle
// MCP Client
implementation 'org.springframework.ai:spring-ai-mcp-client-spring-boot-starter'

// WebFlux Support
implementation 'org.springframework.boot:spring-boot-starter-webflux'

// Web Support
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-undertow'

// Database
implementation 'com.h2database:h2'
```

## Configuration

The module is configured to use:

- Spring Boot with WebFlux for reactive programming
- H2 database for development
- Undertow as the web server
- Spring AI MCP client for AI integration
