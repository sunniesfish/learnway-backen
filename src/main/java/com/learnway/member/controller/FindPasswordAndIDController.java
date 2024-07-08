package com.learnway.member.controller;

import com.learnway.member.service.FindPasswordAndIDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/password")
@RequiredArgsConstructor
public class FindPasswordAndIDController {

    private final FindPasswordAndIDService findPasswordAndIDService;

    // 패스워드 find 폼
    @GetMapping("/find")
    public String showPasswordFindForm() {
        return "member/password-find";
    }
}
