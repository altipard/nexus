package de.zilicon.nexus.domain.graph

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property
import org.springframework.data.neo4j.core.schema.Relationship
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import java.time.Instant

/**
 * Generic entity node in the dependency graph
 *
 * This is the core building block of the graph database.
 * All entities (Projects, Teams, Users, Services, etc.) are represented as EntityNode
 * with their specific attributes stored in the properties map.
 *
 * @property id Unique identifier (UUID)
 * @property type Entity type (references EntityType definition from PostgreSQL)
 * @property properties Dynamic key-value properties specific to this entity type
 * @property createdAt Timestamp when entity was created
 * @property updatedAt Timestamp when entity was last modified
 * @property owner User or department that owns this entity
 * @property readers List of users/groups with read access
 * @property writers List of users/groups with write access
 * @property visibility Access control level
 */
@Node("Entity")
data class EntityNode(
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator::class)
    val id: String? = null,

    /**
     * Type of entity (e.g., "Project", "Team", "User", "Service")
     * References EntityType definition stored in PostgreSQL
     */
    @Property("type")
    val type: String,

    /**
     * Entity name (e.g., "Payment System", "Platform Team", "John Doe")
     */
    @Property("name")
    val name: String,

    /**
     * JSON string containing dynamic properties specific to this entity type
     * Examples:
     * - Project: {"status": "ACTIVE", "priority": "HIGH", "tech_stack": "Kotlin, Spring Boot"}
     * - Team: {"size": 12, "location": "Berlin", "focus": "Backend"}
     * - User: {"email": "john@zilicon.de", "role": "Developer"}
     */
    @Property("properties_json")
    val propertiesJson: String = "{}",

    @Property("created_at")
    val createdAt: Instant = Instant.now(),

    @Property("updated_at")
    val updatedAt: Instant = Instant.now(),

    // Access Control List (ACL) fields
    @Property("owner")
    val owner: String,

    @Property("readers")
    val readers: List<String> = emptyList(),

    @Property("writers")
    val writers: List<String> = emptyList(),

    @Property("visibility")
    val visibility: Visibility = Visibility.DEPARTMENT,

    /**
     * Outgoing relationships from this entity
     */
    @Relationship(type = "RELATED_TO", direction = Relationship.Direction.OUTGOING)
    val dependencies: List<DependencyRelationship> = emptyList()
) {
    /**
     * Check if a user can read this entity
     */
    fun canRead(userId: String, userDepartment: String): Boolean {
        return when (visibility) {
            Visibility.PUBLIC -> true
            Visibility.DEPARTMENT -> userDepartment == owner
            Visibility.RESTRICTED -> userId in readers || userId == owner
            Visibility.PRIVATE -> userId == owner
        }
    }

    /**
     * Check if a user can write to this entity
     */
    fun canWrite(userId: String): Boolean {
        return userId == owner || userId in writers
    }
}
