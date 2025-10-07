package de.zilicon.nexus.controller

import de.zilicon.nexus.dto.EntityResponse
import de.zilicon.nexus.service.GraphService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for graph dependency operations
 */
@RestController
@RequestMapping("/api/graph")
@Tag(name = "Graph", description = "Dependency graph operations")
class GraphController(
    private val graphService: GraphService
) {

    @GetMapping("/dependencies/{entityId}")
    @Operation(summary = "Find all dependencies of an entity")
    fun findDependencies(
        @PathVariable entityId: String,
        @RequestParam(defaultValue = "5") depth: Int
    ): ResponseEntity<List<EntityResponse>> {
        val response = graphService.findDependencies(entityId, depth)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/dependents/{entityId}")
    @Operation(summary = "Find all entities that depend on this entity")
    fun findDependents(@PathVariable entityId: String): ResponseEntity<List<EntityResponse>> {
        val response = graphService.findDependents(entityId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/bottlenecks")
    @Operation(summary = "Find bottleneck entities (most depended upon)")
    fun findBottlenecks(@RequestParam(defaultValue = "10") limit: Int): ResponseEntity<List<EntityResponse>> {
        val response = graphService.findBottlenecks(limit)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/circular-dependencies")
    @Operation(summary = "Find circular dependencies in the graph")
    fun findCircularDependencies(): ResponseEntity<List<List<EntityResponse>>> {
        val response = graphService.findCircularDependencies()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/shortest-path")
    @Operation(summary = "Find shortest dependency path between two entities")
    fun findShortestPath(
        @RequestParam sourceId: String,
        @RequestParam targetId: String
    ): ResponseEntity<List<EntityResponse>> {
        val response = graphService.findShortestPath(sourceId, targetId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/accessible")
    @Operation(summary = "Find entities accessible to a user")
    fun findAccessibleEntities(
        @RequestParam userId: String,
        @RequestParam userDepartment: String
    ): ResponseEntity<List<EntityResponse>> {
        val response = graphService.findAccessibleEntities(userId, userDepartment)
        return ResponseEntity.ok(response)
    }
}
