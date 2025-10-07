# Nexus - Dependency & Resource Management System

Enterprise-grade dependency and resource management engine for Banking IT environments.

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Docker Desktop (for PostgreSQL + Neo4j)

### Option 1: Quick Start with H2 (No Docker)

```bash
# Simple in-memory database
./start.sh
```

Access at http://localhost:8080

### Option 2: Full Stack with Docker (PostgreSQL + Neo4j)

```bash
# Start PostgreSQL + Neo4j + Application
./start-local.sh

# Stop databases
./stop-local.sh
```

**What it starts:**
- ✅ PostgreSQL on `localhost:5432`
- ✅ Neo4j on `localhost:7474` (Browser) and `localhost:7687` (Bolt)
- ✅ Spring Boot on `localhost:8080`

### 2. Verify

```bash
# Health endpoint
curl http://localhost:8080/api/health

# Neo4j Browser
open http://localhost:7474
# Username: neo4j
# Password: password123
```

### 3. Manual Docker Commands

```bash
# Start databases only
docker-compose up -d

# View logs
docker-compose logs -f

# Stop databases
docker-compose down

# Stop and delete data
docker-compose down -v
```

## Project Structure

```
nexus/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/bank/nexus/
│   │   │       ├── NexusApplication.kt
│   │   │       └── controller/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── build.gradle.kts
└── settings.gradle.kts
```

## Technology Stack

- **Language**: Kotlin 1.9.22
- **Framework**: Spring Boot 3.2.2
- **Build**: Gradle 8.x (Kotlin DSL)
- **Java**: 21
- **Database (MVP)**: H2 in-memory
- **Database (Production)**: PostgreSQL 15 + Neo4j 5
- **API**: REST (Spring MVC)

## MVP Development Roadmap

### ✅ Phase 0: Minimal Setup (DONE)
- [x] Spring Boot project structure
- [x] H2 in-memory database
- [x] Health check endpoint
- [x] Build & run successfully

### 🔄 Phase 1: Core Domain (Week 1)
- [ ] Create domain models (Project, Team, User, Skill)
- [ ] Add JPA repositories
- [ ] Implement REST CRUD endpoints
- [ ] Add sample data initializer

### 🔄 Phase 2: Graph Visualization (Week 2)
- [ ] Add graph data structures
- [ ] Create graph API endpoints
- [ ] (Frontend) Cytoscape.js integration
- [ ] Interactive dependency visualization

### 🔄 Phase 3: AI Integration (Week 3)
- [ ] Integrate Ollama
- [ ] Natural language query endpoint
- [ ] Query translation (NL → Domain queries)
- [ ] Chat interface

### 🔄 Phase 4: Dashboard & Analytics (Week 4)
- [ ] Analytics algorithms (bottlenecks, gaps)
- [ ] Dashboard API endpoints
- [ ] CSV import functionality
- [ ] (Frontend) KPI visualizations

## Production Migration

When ready to move beyond MVP:

1. **Switch to PostgreSQL**: Update `application.yml`
2. **Add Neo4j**: Add dependency + configuration
3. **Add Kafka**: Event sourcing setup
4. **Add Security**: JWT authentication
5. **Deploy to OpenShift**: Container deployment

## Documentation

- [Architecture](ARCHITECTURE.md) - Full enterprise architecture
- [MVP Strategy](POTENTIAL.md) - 4-week MVP plan
