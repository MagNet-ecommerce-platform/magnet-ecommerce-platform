package com.magnet.ecommerce.identity_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.magnet.ecommerce.identity_service.config.TestSecurityConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IdentityController.class)
@Import(TestSecurityConfig.class)
class IdentityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnIdentityServiceMessage() throws Exception {
        mockMvc.perform(get("/api/v1/identity/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Identity Service is running"));
    }
}