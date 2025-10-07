package de.zilicon.nexus.controller

import de.zilicon.nexus.domain.graph.Criticality
import de.zilicon.nexus.domain.graph.DependencyRelationship
import de.zilicon.nexus.domain.graph.EntityNode
import de.zilicon.nexus.domain.graph.Visibility
import de.zilicon.nexus.repository.EntityRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GraphControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var entityRepository: EntityRepository

    @BeforeEach
    fun cleanup() {
        entityRepository.deleteAll()
    }

    @Test
    fun `should find dependencies of an entity`() {
        // First create and save the target entity
        val database = EntityNode(
            type = "Database",
            name = "PostgreSQL",
            owner = "team1"
        )
        val savedDatabase = entityRepository.save(database)

        // Create API service with dependency
        val apiService = EntityNode(
            type = "Service",
            name = "API Service",
            owner = "team1",
            dependencies = listOf(
                DependencyRelationship(
                    type = "database",
                    criticality = Criticality.HIGH,
                    target = savedDatabase
                )
            )
        )
        val savedApiService = entityRepository.save(apiService)

        // Should return the database dependency
        mockMvc.perform(get("/api/graph/dependencies/${savedApiService.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("PostgreSQL"))
    }

    @Test
    fun `should find dependencies with custom depth`() {
        // Create a chain: Frontend -> API -> Database
        // Save entities in order: Database first, then API, then Frontend
        val database = EntityNode(
            type = "Database",
            name = "PostgreSQL",
            owner = "team1"
        )
        val savedDatabase = entityRepository.save(database)

        val apiService = EntityNode(
            type = "Service",
            name = "API Service",
            owner = "team1",
            dependencies = listOf(
                DependencyRelationship(
                    type = "database",
                    criticality = Criticality.HIGH,
                    target = savedDatabase
                )
            )
        )
        val savedApiService = entityRepository.save(apiService)

        val frontend = EntityNode(
            type = "Service",
            name = "Frontend",
            owner = "team1",
            dependencies = listOf(
                DependencyRelationship(
                    type = "api",
                    criticality = Criticality.MEDIUM,
                    target = savedApiService
                )
            )
        )
        val savedFrontend = entityRepository.save(frontend)

        // Should return both API and Database (depth 2)
        mockMvc.perform(get("/api/graph/dependencies/${savedFrontend.id}?depth=3"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    fun `should find dependents of an entity`() {
        val entity1 = entityRepository.save(
            EntityNode(
                type = "Database",
                name = "PostgreSQL",
                owner = "team1"
            )
        )

        mockMvc.perform(get("/api/graph/dependents/${entity1.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should find bottlenecks`() {
        mockMvc.perform(get("/api/graph/bottlenecks"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should find bottlenecks with custom limit`() {
        mockMvc.perform(get("/api/graph/bottlenecks?limit=5"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should find circular dependencies`() {
        mockMvc.perform(get("/api/graph/circular-dependencies"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should find shortest path between entities`() {
        val entity1 = entityRepository.save(
            EntityNode(
                type = "Service",
                name = "Service A",
                owner = "team1"
            )
        )

        val entity2 = entityRepository.save(
            EntityNode(
                type = "Service",
                name = "Service B",
                owner = "team2"
            )
        )

        mockMvc.perform(
            get("/api/graph/shortest-path")
                .param("sourceId", entity1.id!!)
                .param("targetId", entity2.id!!)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should find accessible entities for user`() {
        // Create entities with different visibility levels
        entityRepository.save(
            EntityNode(
                type = "Project",
                name = "Public Project",
                owner = "dept1",
                visibility = Visibility.PUBLIC
            )
        )

        entityRepository.save(
            EntityNode(
                type = "Project",
                name = "Department Project",
                owner = "dept1",
                visibility = Visibility.DEPARTMENT
            )
        )

        entityRepository.save(
            EntityNode(
                type = "Project",
                name = "Private Project",
                owner = "user1",
                visibility = Visibility.PRIVATE
            )
        )

        mockMvc.perform(
            get("/api/graph/accessible")
                .param("userId", "user1")
                .param("userDepartment", "dept1")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
    }

    @Test
    fun `should return empty list when no shortest path exists`() {
        val entity1 = entityRepository.save(
            EntityNode(
                type = "Service",
                name = "Isolated Service A",
                owner = "team1"
            )
        )

        val entity2 = entityRepository.save(
            EntityNode(
                type = "Service",
                name = "Isolated Service B",
                owner = "team2"
            )
        )

        mockMvc.perform(
            get("/api/graph/shortest-path")
                .param("sourceId", entity1.id!!)
                .param("targetId", entity2.id!!)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(0))
    }
}
