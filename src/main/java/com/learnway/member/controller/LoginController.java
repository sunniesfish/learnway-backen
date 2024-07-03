package com.learnway.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // 로그인 방식 선택 - 일반 > 컨설트
    @GetMapping("/consult/loginChange/consult")
    public String loginChange_consult() {
        return "index-consult";
    }

    // 로그인 방식 선택 - 컨설트 > 일반
    @GetMapping("/loginChange/member")
    public String loginChange_member() {
        return "index";
    }

    // 시큐리티 로그인 성공 후 Redirect 경로 Get 매핑
    @GetMapping("/loginOk")
    public String loginOK() {
        return "loginOk";
    }
    // 임시 테스트 로깅
    @GetMapping("/consult/home")
    public String consultHome() {
        return "consult-home";
    }
}
