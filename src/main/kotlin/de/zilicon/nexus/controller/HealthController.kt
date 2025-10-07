package de.zilicon.nexus.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Health", description = "Health check")
@RequestMapping("/api")
class HealthController {

    companion object {
        private const val STATUS_UP = "UP"
        private const val APPLICATION_NAME = "Nexus"
        private const val DEFAULT_VERSION = "0.0.1-SNAPSHOT"
    }

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to STATUS_UP,
            "application" to APPLICATION_NAME,
            "version" to DEFAULT_VERSION
        )
    }
}