package de.zilicon.nexus.controller

import de.zilicon.nexus.domain.graph.Visibility
import de.zilicon.nexus.dto.CreateEntityRequest
import de.zilicon.nexus.dto.UpdateEntityRequest
import de.zilicon.nexus.repository.EntityRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import com.fasterxml.jackson.databind.ObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EntityControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var entityRepository: EntityRepository

    @BeforeEach
    fun cleanup() {
        entityRepository.deleteAll()
    }

    @Test
    fun `should create entity successfully`() {
        val request = CreateEntityRequest(
            type = "Project",
            name = "Test Project",
            propertiesJson = """{"status": "ACTIVE"}""",
            owner = "user1",
            visibility = Visibility.PUBLIC
        )

        mockMvc.perform(
            post("/api/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.type").value("Project"))
            .andExpect(jsonPath("$.name").value("Test Project"))
            .andExpect(jsonPath("$.owner").value("user1"))
            .andExpect(jsonPath("$.visibility").value("PUBLIC"))
    }

    @Test
    fun `should get entity by ID`() {
        // Create entity first
        val request = CreateEntityRequest(
            type = "Team",
            name = "Engineering Team",
            propertiesJson = """{"size": 10}""",
            owner = "dept1"
        )

        val createResponse = mockMvc.perform(
            post("/api/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val createdEntity = objectMapper.readTree(createResponse.response.contentAsString)
        val entityId = createdEntity.get("id").asText()

        // Get entity by ID
        mockMvc.perform(get("/api/entities/$entityId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(entityId))
            .andExpect(jsonPath("$.type").value("Team"))
            .andExpect(jsonPath("$.name").value("Engineering Team"))
    }

    @Test
    fun `should return 404 when entity not found`() {
        val nonExistentId = "non-existent-id"

        mockMvc.perform(get("/api/entities/$nonExistentId"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.timestamp").exists())
    }

    @Test
    fun `should get all entities`() {
        // Create multiple entities
        val request1 = CreateEntityRequest(
            type = "Project",
            name = "Project 1",
            owner = "user1"
        )
        val request2 = CreateEntityRequest(
            type = "Team",
            name = "Team 1",
            owner = "user2"
        )

        mockMvc.perform(
            post("/api/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
        ).andExpect(status().isCreated)

        mockMvc.perform(
            post("/api/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2))
        ).andExpect(status().isCreated)

        // Get all entities
        mockMvc.perform(get("/api/entities"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    fun `should filter entities by type`() {
        // Create entities of different types
        val projectRequest = CreateEntityRequest(
            type = "Project",
            name = "Project 1",
            owner = "user1"
        )
        val teamRequest = CreateEntityRequest(
            type = "Team",
            name = "Team 1",
            owner = "user2"
        )

        mockMvc.perform(
            post("/api/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectRequest))
        ).andExpect(status().isCreated)

        mockMvc.perform(
            post("/api/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teamRequest))
        ).andExpect(status().isCreated)

        // Filter by type
        mockMvc.perform(get("/api/entities?type=Project"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].type").value("Project"))
    }

    @Test
    fun `should update entity successfully`() {
        // Create entity
        val createRequest = CreateEntityRequest(
            type = "Project",
            name = "Original Name",
            propertiesJson = """{"status": "ACTIVE"}""",
            owner = "user1"
        )

        val createResponse = mockMvc.perform(
            post("/api/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val createdEntity = objectMapper.readTree(createResponse.response.contentAsString)
        val entityId = createdEntity.get("id").asText()

        // Update entity
        val updateRequest = UpdateEntityRequest(
            name = "Updated Name",
            propertiesJson = """{"status": "COMPLETED"}""",
            visibility = Visibility.PRIVATE
        )

        mockMvc.perform(
            put("/api/entities/$entityId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(entityId))
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.propertiesJson").value("""{"status": "COMPLETED"}"""))
            .andExpect(jsonPath("$.visibility").value("PRIVATE"))
    }

    @Test
    fun `should delete entity successfully`() {
        // Create entity
        val request = CreateEntityRequest(
            type = "Project",
            name = "To Be Deleted",
            owner = "user1"
        )

        val createResponse = mockMvc.perform(
            post("/api/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val createdEntity = objectMapper.readTree(createResponse.response.contentAsString)
        val entityId = createdEntity.get("id").asText()

        // Delete entity
        mockMvc.perform(delete("/api/entities/$entityId"))
            .andExpect(status().isNoContent)

        // Verify deletion
        mockMvc.perform(get("/api/entities/$entityId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should return 404 when deleting non-existent entity`() {
        val nonExistentId = "non-existent-id"

        mockMvc.perform(delete("/api/entities/$nonExistentId"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
    }
}
