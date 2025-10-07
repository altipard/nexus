package de.zilicon.nexus.service

import de.zilicon.nexus.dto.EntityResponse
import de.zilicon.nexus.repository.EntityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service layer for graph dependency operations
 */
@Service
@Transactional(readOnly = true)
class GraphService(
    private val entityRepository: EntityRepository
) {

    /**
     * Find all dependencies of an entity up to a given depth
     */
    fun findDependencies(entityId: String, depth: Int = 5): List<EntityResponse> {
        return entityRepository.findDependencies(entityId, depth)
            .map { EntityResponse.fromEntity(it) }
    }

    /**
     * Find all entities that depend on the given entity
     */
    fun findDependents(entityId: String): List<EntityResponse> {
        return entityRepository.findDependents(entityId)
            .map { EntityResponse.fromEntity(it) }
    }

    /**
     * Find bottleneck entities (most depended upon)
     */
    fun findBottlenecks(limit: Int = 10): List<EntityResponse> {
        return entityRepository.findBottlenecks(limit)
            .map { EntityResponse.fromEntity(it) }
    }

    /**
     * Find circular dependencies in the graph
     */
    fun findCircularDependencies(): List<List<EntityResponse>> {
        return entityRepository.findCircularDependencies()
            .map { cycle ->
                cycle.map { entity -> EntityResponse.fromEntity(entity) }
            }
    }

    /**
     * Find shortest dependency path between two entities
     */
    fun findShortestPath(sourceId: String, targetId: String): List<EntityResponse> {
        return entityRepository.findShortestPath(sourceId, targetId)
            ?.map { EntityResponse.fromEntity(it) }
            ?: emptyList()
    }

    /**
     * Find entities accessible to a user based on visibility rules
     */
    fun findAccessibleEntities(userId: String, userDepartment: String): List<EntityResponse> {
        return entityRepository.findAccessibleEntities(userId, userDepartment)
            .map { EntityResponse.fromEntity(it) }
    }
}
