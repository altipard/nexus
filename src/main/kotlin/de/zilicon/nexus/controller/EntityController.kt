package de.zilicon.nexus.controller

import de.zilicon.nexus.dto.CreateEntityRequest
import de.zilicon.nexus.dto.EntityResponse
import de.zilicon.nexus.dto.UpdateEntityRequest
import de.zilicon.nexus.service.EntityService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for entity operations
 */
@RestController
@RequestMapping("/api/entities")
@Tag(name = "Entities", description = "Entity management operations")
class EntityController(
    private val entityService: EntityService
) {

    @PostMapping
    @Operation(summary = "Create a new entity")
    fun createEntity(@RequestBody request: CreateEntityRequest): ResponseEntity<EntityResponse> {
        val response = entityService.createEntity(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get entity by ID")
    fun getEntity(@PathVariable id: String): ResponseEntity<EntityResponse> {
        val response = entityService.getEntity(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    @Operation(summary = "Get all entities or filter by type")
    fun getEntities(@RequestParam(required = false) type: String?): ResponseEntity<List<EntityResponse>> {
        val response = if (type != null) {
            entityService.getEntitiesByType(type)
        } else {
            entityService.getAllEntities()
        }
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an entity")
    fun updateEntity(
        @PathVariable id: String,
        @RequestBody request: UpdateEntityRequest
    ): ResponseEntity<EntityResponse> {
        val response = entityService.updateEntity(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an entity")
    fun deleteEntity(@PathVariable id: String): ResponseEntity<Void> {
        entityService.deleteEntity(id)
        return ResponseEntity.noContent().build()
    }
}
