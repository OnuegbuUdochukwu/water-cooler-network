package com.codewithudo.backend;

import com.codewithudo.backend.dto.UserLoginDto;
import com.codewithudo.backend.dto.UserRegistrationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_then_login_then_me() throws Exception {
        UserRegistrationDto reg = new UserRegistrationDto();
        reg.setName("Test User");
        reg.setEmail("test@example.com");
        reg.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists());

        UserLoginDto login = new UserLoginDto();
        login.setEmail("test@example.com");
        login.setPassword("password123");

        String token = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String jwt = objectMapper.readTree(token).get("token").asText();

        var meResult = mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + jwt))
            .andReturn();
        System.out.println("/api/auth/me status: " + meResult.getResponse().getStatus());
        System.out.println("/api/auth/me body: " + meResult.getResponse().getContentAsString());
        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + jwt))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}


