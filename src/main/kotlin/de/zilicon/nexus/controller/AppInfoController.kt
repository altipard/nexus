package de.zilicon.nexus.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.info.BuildProperties
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AppInfoController(
    private val buildProperties: BuildProperties?
) {

    @Value("\${spring.application.name:nexus}")
    private lateinit var appName: String

    @GetMapping("/api/info")
    fun getAppInfo(): Map<String, String> {
        val version = buildProperties?.version ?: "0.0.1-SNAPSHOT"
        return mapOf(
            "name" to appName,
            "version" to version
        )
    }
}
