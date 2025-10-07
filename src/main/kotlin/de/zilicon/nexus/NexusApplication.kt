package de.zilicon.nexus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NexusApplication

fun main(args: Array<String>) {
    runApplication<NexusApplication>(*args)
}
