package de.zilicon.nexus.dto

import de.zilicon.nexus.domain.graph.EntityNode
import de.zilicon.nexus.domain.graph.Visibility
import java.time.Instant

/**
 * Response DTO for entity data
 */
data class EntityResponse(
    val id: String,
    val type: String,
    val name: String,
    val propertiesJson: String,
    val owner: String,
    val readers: List<String>,
    val writers: List<String>,
    val visibility: Visibility,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromEntity(entity: EntityNode): EntityResponse {
            return EntityResponse(
                id = entity.id ?: throw IllegalStateException("Entity ID cannot be null"),
                type = entity.type,
                name = entity.name,
                propertiesJson = entity.propertiesJson,
                owner = entity.owner,
                readers = entity.readers,
                writers = entity.writers,
                visibility = entity.visibility,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
