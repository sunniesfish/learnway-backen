package com.learnway.member.controller;

import com.learnway.member.dto.JoinDTO;
import com.learnway.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    // Get 요청 시 회원가입 폼 리턴
    @GetMapping("/join")
    public String join(Model model) {
        return "join";
    }

    // Post 요청 시 회원 가입 처리
    @PostMapping("/join")
    public String join(JoinDTO joinDTO){
        memberService.joinMember(joinDTO);
        return "redirect:/";
    }
}
