package de.zilicon.nexus.service

import de.zilicon.nexus.domain.graph.EntityNode
import de.zilicon.nexus.dto.CreateEntityRequest
import de.zilicon.nexus.dto.EntityResponse
import de.zilicon.nexus.dto.UpdateEntityRequest
import de.zilicon.nexus.exception.EntityNotFoundException
import de.zilicon.nexus.repository.EntityRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

/**
 * Service layer for entity operations
 */
@Service
@Transactional
class EntityService(
    private val entityRepository: EntityRepository
) {

    /**
     * Create a new entity
     */
    fun createEntity(request: CreateEntityRequest): EntityResponse {
        val entity = EntityNode(
            type = request.type,
            name = request.name,
            propertiesJson = request.propertiesJson,
            owner = request.owner,
            readers = request.readers,
            writers = request.writers,
            visibility = request.visibility,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val savedEntity = entityRepository.save(entity)
        return EntityResponse.fromEntity(savedEntity)
    }

    /**
     * Get entity by ID
     */
    @Transactional(readOnly = true)
    fun getEntity(id: String): EntityResponse {
        val entity = entityRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(id)
        return EntityResponse.fromEntity(entity)
    }

    /**
     * Get all entities of a specific type
     */
    @Transactional(readOnly = true)
    fun getEntitiesByType(type: String): List<EntityResponse> {
        return entityRepository.findByType(type)
            .map { EntityResponse.fromEntity(it) }
    }

    /**
     * Update an existing entity
     */
    fun updateEntity(id: String, request: UpdateEntityRequest): EntityResponse {
        val entity = entityRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(id)

        val updatedEntity = entity.copy(
            name = request.name ?: entity.name,
            propertiesJson = request.propertiesJson ?: entity.propertiesJson,
            readers = request.readers ?: entity.readers,
            writers = request.writers ?: entity.writers,
            visibility = request.visibility ?: entity.visibility,
            updatedAt = Instant.now()
        )

        val savedEntity = entityRepository.save(updatedEntity)
        return EntityResponse.fromEntity(savedEntity)
    }

    /**
     * Delete an entity
     */
    fun deleteEntity(id: String) {
        if (!entityRepository.existsById(id)) {
            throw EntityNotFoundException(id)
        }
        entityRepository.deleteById(id)
    }

    /**
     * Get all entities
     */
    @Transactional(readOnly = true)
    fun getAllEntities(): List<EntityResponse> {
        return entityRepository.findAll()
            .map { EntityResponse.fromEntity(it) }
    }
}
