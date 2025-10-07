package de.zilicon.nexus.config

import de.zilicon.nexus.domain.graph.Criticality
import de.zilicon.nexus.domain.graph.DependencyRelationship
import de.zilicon.nexus.domain.graph.EntityNode
import de.zilicon.nexus.domain.graph.Visibility
import de.zilicon.nexus.repository.EntityRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.Instant

/**
 * Loads test data into Neo4j graph database on application startup
 * Only active in 'local' profile
 */
@Configuration
@Profile("local")
class TestDataLoader {

    private val logger = LoggerFactory.getLogger(TestDataLoader::class.java)

    @Bean
    fun loadTestData(entityRepository: EntityRepository) = CommandLineRunner {
        logger.info("🚀 Loading test data into Neo4j...")

        // Clear existing data (optional - remove if you want to keep data between restarts)
        entityRepository.deleteAll()
        logger.info("🗑️  Cleared existing data")

        // === PROJECTS ===
        val paymentSystem = EntityNode(
            type = "Project",
            name = "Payment System",
            propertiesJson = """{"status":"ACTIVE","priority":"HIGH","description":"Core payment processing service","tech_stack":"Kotlin, Spring Boot, PostgreSQL"}""",
            owner = "platform-team",
            readers = listOf("platform-team", "finance-team"),
            writers = listOf("platform-team"),
            visibility = Visibility.DEPARTMENT
        )

        val userService = EntityNode(
            type = "Project",
            name = "User Service",
            propertiesJson = """{"status":"ACTIVE","priority":"MEDIUM","description":"User authentication and management","tech_stack":"Kotlin, Spring Boot, Neo4j"}""",
            owner = "platform-team",
            visibility = Visibility.DEPARTMENT
        )

        val analyticsEngine = EntityNode(
            type = "Project",
            name = "Analytics Engine",
            propertiesJson = """{"status":"PLANNING","priority":"MEDIUM","description":"Real-time analytics processing","tech_stack":"Python, Apache Kafka, ClickHouse"}""",
            owner = "data-team",
            visibility = Visibility.DEPARTMENT
        )

        val notificationService = EntityNode(
            type = "Project",
            name = "Notification Service",
            propertiesJson = """{"status":"ACTIVE","priority":"LOW","description":"Email and push notification service","tech_stack":"Node.js, RabbitMQ, Redis"}""",
            owner = "platform-team",
            visibility = Visibility.PUBLIC
        )

        // === TEAMS ===
        val platformTeam = EntityNode(
            type = "Team",
            name = "Platform Team",
            propertiesJson = """{"size":8,"focus":"Core infrastructure and services","location":"Berlin"}""",
            owner = "platform-team",
            visibility = Visibility.PUBLIC
        )

        val dataTeam = EntityNode(
            type = "Team",
            name = "Data Team",
            propertiesJson = """{"size":5,"focus":"Analytics and data processing","location":"Remote"}""",
            owner = "data-team",
            visibility = Visibility.PUBLIC
        )

        // === SKILLS ===
        val kotlinSkill = EntityNode(
            type = "Skill",
            name = "Kotlin",
            propertiesJson = """{"category":"Programming Language","difficulty":"INTERMEDIATE"}""",
            owner = "system",
            visibility = Visibility.PUBLIC
        )

        val springBootSkill = EntityNode(
            type = "Skill",
            name = "Spring Boot",
            propertiesJson = """{"category":"Framework","difficulty":"INTERMEDIATE"}""",
            owner = "system",
            visibility = Visibility.PUBLIC
        )

        val kafkaSkill = EntityNode(
            type = "Skill",
            name = "Apache Kafka",
            propertiesJson = """{"category":"Message Broker","difficulty":"ADVANCED"}""",
            owner = "system",
            visibility = Visibility.PUBLIC
        )

        // === USERS ===
        val alice = EntityNode(
            type = "User",
            name = "Alice Schmidt",
            propertiesJson = """{"email":"alice@example.com","role":"Senior Backend Developer","experience_years":7}""",
            owner = "alice",
            visibility = Visibility.DEPARTMENT
        )

        val bob = EntityNode(
            type = "User",
            name = "Bob Mueller",
            propertiesJson = """{"email":"bob@example.com","role":"Data Engineer","experience_years":5}""",
            owner = "bob",
            visibility = Visibility.DEPARTMENT
        )

        // Save all entities (without relationships first)
        val savedPayment = entityRepository.save(paymentSystem)
        val savedUser = entityRepository.save(userService)
        val savedAnalytics = entityRepository.save(analyticsEngine)
        val savedNotification = entityRepository.save(notificationService)
        val savedPlatformTeam = entityRepository.save(platformTeam)
        val savedDataTeam = entityRepository.save(dataTeam)
        val savedKotlin = entityRepository.save(kotlinSkill)
        val savedSpringBoot = entityRepository.save(springBootSkill)
        val savedKafka = entityRepository.save(kafkaSkill)
        val savedAlice = entityRepository.save(alice)
        val savedBob = entityRepository.save(bob)

        logger.info("✅ Created ${entityRepository.count()} entities")

        // === CREATE RELATIONSHIPS ===

        // Payment System dependencies
        val paymentDependsOnUser = savedPayment.copy(
            dependencies = listOf(
                DependencyRelationship(
                    type = "DEPENDS_ON",
                    target = savedUser,
                    criticality = Criticality.HIGH,
                    propertiesJson = """{"reason":"Requires user authentication","since":"2024-01-15"}"""
                )
            )
        )
        entityRepository.save(paymentDependsOnUser)

        // Analytics Engine dependencies
        val analyticsWithDeps = savedAnalytics.copy(
            dependencies = listOf(
                DependencyRelationship(
                    type = "DEPENDS_ON",
                    target = savedPayment,
                    criticality = Criticality.MEDIUM,
                    propertiesJson = """{"reason":"Analyzes payment transactions","data_type":"event_stream"}"""
                ),
                DependencyRelationship(
                    type = "DEPENDS_ON",
                    target = savedUser,
                    criticality = Criticality.MEDIUM,
                    propertiesJson = """{"reason":"User behavior analytics"}"""
                )
            )
        )
        entityRepository.save(analyticsWithDeps)

        // Notification Service dependencies
        val notificationWithDeps = savedNotification.copy(
            dependencies = listOf(
                DependencyRelationship(
                    type = "DEPENDS_ON",
                    target = savedPayment,
                    criticality = Criticality.LOW,
                    propertiesJson = """{"reason":"Payment confirmation emails","notification_types":"email, push"}"""
                )
            )
        )
        entityRepository.save(notificationWithDeps)

        // Team ownership relationships
        val platformOwnsPayment = savedPlatformTeam.copy(
            dependencies = listOf(
                DependencyRelationship(
                    type = "OWNS",
                    target = savedPayment,
                    criticality = Criticality.BLOCKING,
                    propertiesJson = """{"responsibility":"Full ownership"}"""
                ),
                DependencyRelationship(
                    type = "OWNS",
                    target = savedUser,
                    criticality = Criticality.BLOCKING,
                    propertiesJson = """{"responsibility":"Full ownership"}"""
                ),
                DependencyRelationship(
                    type = "OWNS",
                    target = savedNotification,
                    criticality = Criticality.BLOCKING,
                    propertiesJson = """{"responsibility":"Full ownership"}"""
                )
            )
        )
        entityRepository.save(platformOwnsPayment)

        val dataOwnsAnalytics = savedDataTeam.copy(
            dependencies = listOf(
                DependencyRelationship(
                    type = "OWNS",
                    target = savedAnalytics,
                    criticality = Criticality.BLOCKING,
                    propertiesJson = """{"responsibility":"Full ownership"}"""
                )
            )
        )
        entityRepository.save(dataOwnsAnalytics)

        // Project skill requirements
        val paymentRequiresSkills = entityRepository.findById(savedPayment.id!!).get().copy(
            dependencies = (entityRepository.findById(savedPayment.id!!).get().dependencies ?: emptyList()) + listOf(
                DependencyRelationship(
                    type = "REQUIRES",
                    target = savedKotlin,
                    criticality = Criticality.HIGH,
                    propertiesJson = """{"proficiency":"advanced","mandatory":true}"""
                ),
                DependencyRelationship(
                    type = "REQUIRES",
                    target = savedSpringBoot,
                    criticality = Criticality.HIGH,
                    propertiesJson = """{"proficiency":"expert","mandatory":true}"""
                )
            )
        )
        entityRepository.save(paymentRequiresSkills)

        // User skills
        val aliceWithSkills = savedAlice.copy(
            dependencies = listOf(
                DependencyRelationship(
                    type = "HAS_SKILL",
                    target = savedKotlin,
                    criticality = Criticality.MEDIUM,
                    propertiesJson = """{"proficiency":"expert","years":5}"""
                ),
                DependencyRelationship(
                    type = "HAS_SKILL",
                    target = savedSpringBoot,
                    criticality = Criticality.MEDIUM,
                    propertiesJson = """{"proficiency":"expert","years":6}"""
                )
            )
        )
        entityRepository.save(aliceWithSkills)

        val bobWithSkills = savedBob.copy(
            dependencies = listOf(
                DependencyRelationship(
                    type = "HAS_SKILL",
                    target = savedKafka,
                    criticality = Criticality.MEDIUM,
                    propertiesJson = """{"proficiency":"expert","years":4}"""
                )
            )
        )
        entityRepository.save(bobWithSkills)

        logger.info("✅ Created relationships between entities")
        logger.info("🎉 Test data loading complete!")
        logger.info("📊 Total entities: ${entityRepository.count()}")
        logger.info("🌐 Neo4j Browser: http://localhost:7474")
        logger.info("   Username: neo4j")
        logger.info("   Password: password123")
    }
}
