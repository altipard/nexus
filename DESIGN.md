# Nexus Design Decisions

This document captures key architectural decisions and their rationale for the Nexus dependency management system.

## Relationship Type Architecture

### Context

The Neo4j graph database supports relationships between entities. In Spring Data Neo4j, relationships can be defined in two ways:

1. **Graph Relationship Type**: The actual Neo4j relationship type (edge label) defined via `@Relationship(type = "X")`
2. **Relationship Property**: A string property stored on the relationship for additional categorization

### Problem

Initial implementation had an architectural inconsistency:

- `EntityNode.kt` defined relationships with `@Relationship(type = "RELATED_TO")`
- Repository queries expected `[:DEPENDS_ON]` relationship type
- `DependencyRelationship.kt` documentation mentioned multiple relationship types: DEPENDS_ON, REQUIRES, OWNS, HAS_SKILL
- Tests failed because the graph relationship type didn't match query expectations

This created confusion: Should we use a generic "RELATED_TO" with property-based typing, or specific relationship types like "DEPENDS_ON"?

### Analysis

Three potential solutions were considered:

#### Option A: Single Specific Type (DEPENDS_ON)

**Implementation:**
- Use `DEPENDS_ON` as the Neo4j relationship type
- Use `DependencyRelationship.type` property for sub-categorization
- All relationships are dependencies, categorized by their nature

**Pros:**
- Aligns with project focus (Dependency Core System)
- Matches existing repository queries
- Simple and focused
- Good performance for dependency-specific queries

**Cons:**
- Limited to dependency relationships
- Cannot easily model other relationship semantics (ownership, requirements, etc.)

#### Option B: Generic Type with Property-Based Categorization

**Implementation:**
- Use `RELATED_TO` as the Neo4j relationship type for all relationships
- Use `DependencyRelationship.type` property to store actual type ("DEPENDS_ON", "REQUIRES", etc.)
- Query using property filters: `[r:RELATED_TO WHERE r.type = "DEPENDS_ON"]`

**Pros:**
- Maximum flexibility
- Easy to add new relationship types
- Single relationship model

**Cons:**
- Requires rewriting all repository queries
- Less performant (must filter by property)
- Loses Neo4j native relationship type advantages
- Poor graph visualization (everything looks the same)

#### Option C: Multiple Specific Types (Full Neo4j Pattern)

**Implementation:**
- Define separate relationship collections: `dependencies`, `requirements`, `ownerships`
- Each uses specific Neo4j type: DEPENDS_ON, REQUIRES, OWNS
- `DependencyRelationship.type` becomes sub-categorization within each type

**Pros:**
- True Neo4j best practice
- Best query performance
- Clearest semantics
- Excellent graph visualization

**Cons:**
- More complex domain model
- More code to maintain
- Need to know all relationship types upfront
- Overkill for current MVP scope

### Decision

**Selected: Option A (DEPENDS_ON only)**

**Rationale:**

1. **Project Scope**: The system is a "Dependency Core System" focused on dependency management and analysis
2. **Existing Architecture**: All repository queries are designed around DEPENDS_ON relationships:
   - `findDependencies()`
   - `findDependents()`
   - `findBottlenecks()`
   - `findCircularDependencies()`
   - `findShortestPath()`
3. **MVP Principle**: Start simple, extend as needed
4. **Performance**: Direct relationship type queries are more efficient than property filters

**Implementation:**

```kotlin
@Node("Entity")
data class EntityNode(
    // ... other properties ...

    @Relationship(type = "DEPENDS_ON", direction = Relationship.Direction.OUTGOING)
    val dependencies: List<DependencyRelationship> = emptyList()
)
```

The `DependencyRelationship.type` property is used for categorizing different kinds of dependencies:
- "technical" - Technical/implementation dependencies
- "data" - Data flow dependencies
- "business" - Business logic dependencies
- "api" - API usage dependencies
- "library" - Third-party library dependencies

### Future Extension

If other relationship types are needed (OWNS, REQUIRES, HAS_SKILL), they should be added as separate `@Relationship` collections following Option C pattern:

```kotlin
@Node("Entity")
data class EntityNode(
    @Relationship(type = "DEPENDS_ON", direction = Relationship.Direction.OUTGOING)
    val dependencies: List<DependencyRelationship> = emptyList(),

    @Relationship(type = "REQUIRES", direction = Relationship.Direction.OUTGOING)
    val requirements: List<Requirement> = emptyList(),

    @Relationship(type = "OWNS", direction = Relationship.Direction.OUTGOING)
    val ownerships: List<Ownership> = emptyList()
)
```

This provides clear separation of concerns and maintains Neo4j best practices while allowing the domain model to grow organically.

### Impact

**Code Changes:**
- `EntityNode.kt`: Changed relationship type from "RELATED_TO" to "DEPENDS_ON"
- Tests: Updated to expect "DEPENDS_ON" relationships
- Documentation: Clarified that `DependencyRelationship.type` is for dependency categorization, not alternative relationship types

**No Changes Needed:**
- Repository queries (already using DEPENDS_ON)
- Service layer (works with EntityNode abstraction)
- Controllers (work with service layer abstraction)

### References

- Neo4j Best Practices: https://neo4j.com/docs/getting-started/data-modeling/
- Spring Data Neo4j Documentation: https://docs.spring.io/spring-data/neo4j/docs/current/reference/html/

---

**Decision Date**: 2025-10-08
**Status**: Implemented
**Reviewed By**: Architecture analysis and implementation validation
