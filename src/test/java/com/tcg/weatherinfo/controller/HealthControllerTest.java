package com.tcg.weatherinfo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock JwtTokenProvider so that the filter can be initialized properly

    @Test
    void testHealthCheck_Success() throws Exception {
        // Perform GET request to /api/health
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk()) // Verify status is 200 OK
                .andExpect(jsonPath("$.status").value("UP")) // Verify status is "UP"
                .andExpect(jsonPath("$.timestamp").exists()); // Verify timestamp exists
    }
}
