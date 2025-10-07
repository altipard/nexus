# Nexus - Dependency Core System

A Spring Boot application with PostgreSQL and Neo4j dual-database architecture for dependency management.

## Current Implementation

This project is in early development. Currently implemented:

- Spring Boot 3.2.2 with Kotlin 1.9.22
- PostgreSQL database integration
- Neo4j graph database integration
- Docker Compose setup for local development
- Health monitoring endpoint
- Swagger UI for API documentation
- Neo4j domain model (EntityNode, DependencyRelationship)
- Spring Data Neo4j repositories with Cypher queries

## Technology Stack

**Backend:**
- Kotlin 1.9.22
- Spring Boot 3.2.2
- Spring Data JPA (PostgreSQL)
- Spring Data Neo4j
- Gradle 8.x with Kotlin DSL

**Databases:**
- PostgreSQL 15+ (event store, metadata)
- Neo4j 5.x (graph database)

**Infrastructure:**
- Docker Compose
- Java 21

## Prerequisites

- Java 21 or later
- Docker and Docker Compose
- Gradle 8.x (or use included wrapper)

## Getting Started

### 1. Start Database Services

```bash
docker compose up -d
```

This starts:
- PostgreSQL on port 5432 (credentials: nexus/nexus)
- Neo4j on ports 7474 (HTTP) and 7687 (Bolt) (credentials: neo4j/password123)

### 2. Build the Application

```bash
./gradlew clean build
```

### 3. Run the Application

```bash
./gradlew bootRun
```

### 4. Access the Application

- **Web UI**: http://localhost:8080
- **API Docs**: http://localhost:8080/swagger-ui.html
- **Neo4j Browser**: http://localhost:7474
- **Health Check**: http://localhost:8080/actuator/health

## Project Structure

```
nexus/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/bank/nexus/
│   │   │       ├── controller/
│   │   │       │   └── AppInfoController.kt
│   │   │       ├── domain/
│   │   │       │   └── graph/
│   │   │       │       ├── Criticality.kt
│   │   │       │       ├── DependencyRelationship.kt
│   │   │       │       ├── EntityNode.kt
│   │   │       │       └── Visibility.kt
│   │   │       └── repository/
│   │   │           └── EntityRepository.kt
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       └── static/
│   │           └── index.html
│   └── test/
├── build.gradle.kts
├── docker-compose.yml
└── README.md
```

## Domain Model

### EntityNode

Generic graph entity that can represent any domain object:

```kotlin
@Node("Entity")
data class EntityNode(
    val id: String?,                    // Auto-generated UUID
    val type: String,                   // Entity type (e.g., "Project", "Team")
    val properties: Map<String, Any>,   // Dynamic properties
    val owner: String,                  // Owner user/department
    val readers: List<String>,          // Read access list
    val writers: List<String>,          // Write access list
    val visibility: Visibility          // Access control level
)
```

### DependencyRelationship

Relationship between entities:

```kotlin
@RelationshipProperties
data class DependencyRelationship(
    val id: String?,
    val type: String,                   // Relationship type
    val properties: Map<String, Any>,   // Metadata
    val criticality: Criticality,       // LOW, MEDIUM, HIGH, BLOCKING
    val target: EntityNode
)
```

### Visibility Levels

- `PUBLIC` - All authenticated users
- `DEPARTMENT` - Same department only
- `RESTRICTED` - Explicit reader list
- `PRIVATE` - Owner only

## Repository Queries

The EntityRepository provides Cypher queries for:

- `findDependencies(entityId, depth)` - Find all dependencies
- `findDependents(entityId)` - Find reverse dependencies
- `findBottlenecks(limit)` - Find most critical dependencies
- `findCircularDependencies()` - Detect circular refs
- `findShortestPath(sourceId, targetId)` - Shortest dependency path
- `findAccessibleEntities(userId, dept)` - ACL-filtered queries

## Configuration

### Database Connections

**PostgreSQL:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nexus
    username: nexus
    password: nexus
```

**Neo4j:**
```yaml
spring:
  neo4j:
    uri: bolt://localhost:7687
    authentication:
      username: neo4j
      password: password123
```

### Profiles

- `local` - Local development with Docker (default)

## API Endpoints

Currently implemented:

- `GET /api/info` - Application version information
- `GET /actuator/health` - Health status (PostgreSQL + Neo4j)
- `GET /actuator/metrics` - Application metrics
- `GET /swagger-ui.html` - API documentation

## Development Commands

```bash
# Clean build
./gradlew clean build

# Compile Kotlin only
./gradlew compileKotlin

# Run tests
./gradlew test

# Run application
./gradlew bootRun

# Stop databases
docker compose down
```

## Database Access

**PostgreSQL:**
```bash
docker exec -it nexus-postgres psql -U nexus -d nexus
```

**Neo4j Browser:**
```
Open http://localhost:7474
Username: neo4j
Password: password123
```

## License

Proprietary - All rights reserved.
