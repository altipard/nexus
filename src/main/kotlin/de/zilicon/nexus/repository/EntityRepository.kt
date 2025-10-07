package de.zilicon.nexus.repository

import de.zilicon.nexus.domain.graph.EntityNode
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository

/**
 * Repository for EntityNode operations
 *
 * Provides CRUD operations and custom Cypher queries for graph traversal
 */
@Repository
interface EntityRepository : Neo4jRepository<EntityNode, String> {

    /**
     * Find entities by type
     */
    fun findByType(type: String): List<EntityNode>

    /**
     * Find entities by owner
     */
    fun findByOwner(owner: String): List<EntityNode>

    /**
     * Find all dependencies of an entity up to specified depth
     *
     * @param entityId The source entity ID
     * @param depth Maximum traversal depth (default: 5)
     * @return List of dependent entities
     */
    @Query("""
        MATCH (source:Entity {id: ${'$'}entityId})-[:DEPENDS_ON*1..]->(dep:Entity)
        RETURN DISTINCT dep
    """)
    fun findDependencies(entityId: String, depth: Int = 5): List<EntityNode>

    /**
     * Find all entities that depend on the specified entity
     *
     * @param entityId The target entity ID
     * @return List of entities that depend on this entity
     */
    @Query("""
        MATCH (dependent:Entity)-[:DEPENDS_ON]->(target:Entity {id: ${'$'}entityId})
        RETURN dependent
    """)
    fun findDependents(entityId: String): List<EntityNode>

    /**
     * Find entities with most dependencies (bottlenecks)
     *
     * @param limit Maximum number of results
     * @return List of entities ordered by dependency count
     */
    @Query("""
        MATCH (e:Entity)<-[r:DEPENDS_ON]-(dependent:Entity)
        WHERE r.criticality IN ['HIGH', 'BLOCKING']
        WITH e, count(r) as dependencyCount
        RETURN e
        ORDER BY dependencyCount DESC
        LIMIT ${'$'}limit
    """)
    fun findBottlenecks(limit: Int = 10): List<EntityNode>

    /**
     * Find circular dependencies
     *
     * @return List of entity chains that form circular dependencies
     */
    @Query("""
        MATCH path = (e:Entity)-[:DEPENDS_ON*2..10]->(e)
        RETURN nodes(path)
        LIMIT 100
    """)
    fun findCircularDependencies(): List<List<EntityNode>>

    /**
     * Search entities by property values
     *
     * @param propertyKey The property key to search
     * @param propertyValue The property value to match
     * @return List of matching entities
     */
    @Query("""
        MATCH (e:Entity)
        WHERE e.properties[${'$'}propertyKey] = ${'$'}propertyValue
        RETURN e
    """)
    fun findByProperty(propertyKey: String, propertyValue: Any): List<EntityNode>

    /**
     * Find entities accessible by user (with ACL filtering)
     *
     * @param userId User ID
     * @param userDepartment User's department
     * @return List of entities the user can read
     */
    @Query("""
        MATCH (e:Entity)
        WHERE e.visibility = 'PUBLIC'
           OR e.owner = ${'$'}userId
           OR ${'$'}userId IN e.readers
           OR e.owner = ${'$'}userDepartment
        RETURN e
    """)
    fun findAccessibleEntities(userId: String, userDepartment: String): List<EntityNode>

    /**
     * Count entities by type
     */
    @Query("""
        MATCH (e:Entity)
        WHERE e.type = ${'$'}type
        RETURN count(e)
    """)
    fun countByType(type: String): Long

    /**
     * Find the shortest dependency path between two entities
     *
     * @param sourceId Source entity ID
     * @param targetId Target entity ID
     * @return List of entities forming the shortest path
     */
    @Query("""
        MATCH path = shortestPath(
            (source:Entity {id: ${'$'}sourceId})-[:DEPENDS_ON*]->
            (target:Entity {id: ${'$'}targetId})
        )
        RETURN nodes(path)
    """)
    fun findShortestPath(sourceId: String, targetId: String): List<EntityNode>?
}
