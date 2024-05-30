package com.example.email.controller;

import com.example.email.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserService userService;

    private MockMvc mockMvc;

    @Test
    public void testCreateUsers() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService)).build();

        // 创建用户A
        mockMvc.perform(post("/api/users/register")
                        .param("username", "userE")
                        .param("email", "userE@example.com")
                        .param("password", "passwordE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 创建用户B
        mockMvc.perform(post("/api/users/register")
                        .param("username", "userF")
                        .param("email", "userF@example.com")
                        .param("password", "passwordF")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}