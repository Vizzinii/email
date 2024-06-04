package com.example.email.controller;

import com.example.email.service.UserService;
import com.example.email.Application;
import com.example.email.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
public class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService)).build();
    }

    @Test
    public void testCreateUsers() throws Exception {
        // 使用唯一的测试数据
        String uniqueSuffix = UUID.randomUUID().toString();

        // 创建用户A
        mockMvc.perform(post("/api/users/register")
                        .param("username", "userA" + uniqueSuffix)
                        .param("email", "userA" + uniqueSuffix + "@example.com")
                        .param("password", "passwordA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 创建用户B
        mockMvc.perform(post("/api/users/register")
                        .param("username", "userB" + uniqueSuffix)
                        .param("email", "userB" + uniqueSuffix + "@example.com")
                        .param("password", "passwordA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}