package de.zilicon.nexus.domain.graph

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Property
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode
import java.time.Instant

/**
 * Relationship between two entities in the dependency graph
 *
 * This represents edges in the graph with their own properties.
 * Examples:
 * - Project DEPENDS_ON Project
 * - Project REQUIRES Skill
 * - Team OWNS Project
 * - User HAS_SKILL Skill
 *
 * @property id Unique identifier for this relationship
 * @property type Relationship type (e.g., "DEPENDS_ON", "REQUIRES", "OWNS")
 * @property properties Dynamic key-value properties for this relationship
 * @property criticality How critical this dependency is
 * @property createdAt When this relationship was created
 * @property visibility Access control level for this relationship
 * @property target The target entity of this relationship
 */
@RelationshipProperties
data class DependencyRelationship(
    @Id
    @GeneratedValue
    val id: Long? = null,

    /**
     * Type of relationship (e.g., "DEPENDS_ON", "REQUIRES", "OWNS", "HAS_SKILL")
     * References RelationshipType definition from PostgreSQL
     */
    @Property("type")
    val type: String,

    /**
     * JSON string containing dynamic properties specific to this relationship type
     * Examples:
     * - DEPENDS_ON: {"reason": "Uses API", "since": "2024-01-15"}
     * - REQUIRES: {"proficiency": "expert", "mandatory": true}
     */
    @Property("properties_json")
    val propertiesJson: String = "{}",

    /**
     * How critical this dependency is for the source entity
     */
    @Property("criticality")
    val criticality: Criticality = Criticality.MEDIUM,

    @Property("created_at")
    val createdAt: Instant = Instant.now(),

    /**
     * Access control for this relationship
     * Inherits from source entity if not specified
     */
    @Property("visibility")
    val visibility: Visibility = Visibility.DEPARTMENT,

    /**
     * Target entity of this relationship
     */
    @TargetNode
    val target: EntityNode
) {
    /**
     * Check if this is a critical or blocking dependency
     */
    fun isCritical(): Boolean {
        return criticality == Criticality.HIGH || criticality == Criticality.BLOCKING
    }

    /**
     * Check if this dependency blocks progress
     */
    fun isBlocking(): Boolean {
        return criticality == Criticality.BLOCKING
    }
}
