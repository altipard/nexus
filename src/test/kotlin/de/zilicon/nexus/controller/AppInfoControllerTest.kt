package de.zilicon.nexus.controller

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
class AppInfoControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should return application info`() {
        mockMvc.perform(get("/api/info"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.version").exists())
    }

    @Test
    fun `should return version from build properties`() {
        mockMvc.perform(get("/api/info"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.version").value(org.hamcrest.Matchers.matchesPattern("\\d+\\.\\d+\\.\\d+.*")))
    }
}
