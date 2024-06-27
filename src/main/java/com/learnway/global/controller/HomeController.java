package com.learnway.global.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 인증 정보를 확인하여 로그인 중일 때 로그인폼 요청할 경우 loginOk 페이지로 Redirect 도와주는 컨트롤러
@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        // 현재 사용자의 인증 정보 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 사용자가 인증된 상태인 경우 && 비회원 유저가 아닐 경우 2가지 충족 시 "/loginOk"로 Redirect
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getName().equals("anonymousUser")))  {
            return "redirect:/loginOk";
        }
        return "index";
    }
}
