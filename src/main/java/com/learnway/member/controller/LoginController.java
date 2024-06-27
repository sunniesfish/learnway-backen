package com.learnway.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // 시큐리티 로그인 성공 후 Redirect 경로 Get 매핑
    @GetMapping("/loginOk")
    public String loginOK() {
        return "loginOk";
    }
}
