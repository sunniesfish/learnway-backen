package com.learnway.member.controller;

import com.learnway.member.dto.JoinDTO;
import com.learnway.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    // Get 요청 시 회원가입 폼 리턴
    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("joinDTO", new JoinDTO());
        return "join";
    }

    // Post 요청 시 회원 가입 처리
    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinDTO joinDTO,
                       BindingResult bindingResult,
                       Model model){
        // 유효성 검사 진행 후 에러 메세지 전달
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            }
            return "join";
        }
        // Password / ConfirmPassword 일치 여부 확인
        if (!joinDTO.getPassword().equals(joinDTO.getConfirmPassword())) {
            model.addAttribute("passwordError", "비밀번호가 일치하지 않습니다.");
            return "join";
        }

        memberService.joinMember(joinDTO);
        return "redirect:/member/joinSuccess"; // 회원 가입 성공 시 로그인 창
    }

    // 회원 가입 성공 페이지
    @GetMapping("/joinSuccess")
    public String joinSuccess() {
        return "joinSuccess";
    }
}
