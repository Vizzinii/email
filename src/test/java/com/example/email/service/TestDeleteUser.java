package com.example.email.service;

import com.example.email.entity.UserEntity;
import com.example.email.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class TestDeleteUser {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @Test
    @Transactional
    public void testDeleteUser() {
        // 获取要删除的用户
        UserEntity user = userRepository.findByUsername("userF");
        assertNotNull(user, "User to delete should exist");

        // 删除用户
        userService.deleteUser(user.getUserId());

        // 验证用户已删除
        UserEntity deletedUser = userRepository.findByUsername("userF");
        assertNull(deletedUser, "User should be deleted");
    }
}