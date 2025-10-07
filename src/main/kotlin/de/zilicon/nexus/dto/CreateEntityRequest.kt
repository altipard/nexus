package de.zilicon.nexus.dto

import de.zilicon.nexus.domain.graph.Visibility

/**
 * Request DTO for creating a new entity
 */
data class CreateEntityRequest(
    val type: String,
    val name: String,
    val propertiesJson: String = "{}",
    val owner: String,
    val readers: List<String> = emptyList(),
    val writers: List<String> = emptyList(),
    val visibility: Visibility = Visibility.DEPARTMENT
)
