package de.zilicon.nexus.domain.graph

/**
 * Access control visibility levels for graph entities
 */
enum class Visibility {
    /**
     * All authenticated users can access
     */
    PUBLIC,

    /**
     * Only users from the same department can access
     */
    DEPARTMENT,

    /**
     * Only users in the explicit reader list can access
     */
    RESTRICTED,

    /**
     * Only the owner can access
     */
    PRIVATE
}
