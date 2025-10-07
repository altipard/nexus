package de.zilicon.nexus.dto

import de.zilicon.nexus.domain.graph.Visibility

/**
 * Request DTO for updating an existing entity
 */
data class UpdateEntityRequest(
    val name: String? = null,
    val propertiesJson: String? = null,
    val readers: List<String>? = null,
    val writers: List<String>? = null,
    val visibility: Visibility? = null
)
