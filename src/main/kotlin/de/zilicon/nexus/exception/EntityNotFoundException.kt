package de.zilicon.nexus.exception

/**
 * Exception thrown when an entity is not found in the graph database
 */
class EntityNotFoundException : RuntimeException {

    constructor(entityId: String) : super("Entity with ID '$entityId' not found")

    constructor(type: String, id: String) : super("Entity of type '$type' with ID '$id' not found")
}
