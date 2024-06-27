package com.learnway.consult.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

// Counselor 멀티 로그인 테스트 파일
@Controller
public class CounselorController {

    @GetMapping("/counselor/login")
    public String counselorLogin() {
        return "counselor-login";
    }

    @GetMapping("/counselor/home")
    public String counselorHome() {
        return "counselor-home";
    }

    @GetMapping("/haha/etc")
    public String counsloretc() {
        return "counselor-etc";
    }
}