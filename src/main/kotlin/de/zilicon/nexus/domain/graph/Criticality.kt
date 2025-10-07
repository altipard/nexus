package de.zilicon.nexus.domain.graph

/**
 * Criticality levels for dependencies
 */
enum class Criticality {
    /**
     * Low priority dependency - can be delayed or postponed
     */
    LOW,

    /**
     * Medium priority dependency - should be considered
     */
    MEDIUM,

    /**
     * High priority dependency - critical for success
     */
    HIGH,

    /**
     * Blocking dependency - must be resolved before proceeding
     */
    BLOCKING
}
