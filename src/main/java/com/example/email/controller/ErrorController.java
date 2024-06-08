package com.example.email.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/403")
    public String accessDenied() {
        return "403";  // 返回403错误页面视图
    }

    @GetMapping("/login")
    public String login() {
        return "login";  // 返回登录页面视图
    }
}