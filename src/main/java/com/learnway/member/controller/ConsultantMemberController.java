package com.learnway.member.controller;

import com.learnway.member.dto.ConsultantJoinDTO;
import com.learnway.member.service.ConsultantMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ConsultantMemberController {

    private final ConsultantMemberService consultantMemberService;
    // GET 시 컨설턴트 가입 폼
    @GetMapping("/member/consult/join")
    public String joinForm(Model model) {
        model.addAttribute("consultantJoinDTO", new ConsultantJoinDTO());
        return "member/consultJoin";
    }

    // POST 시 컨설턴트 회원 가입 진행
    @PostMapping("/member/consult/join")
    public String join(@Valid ConsultantJoinDTO consultantJoinDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            }
            return "member/consultJoin";
        }
        try {
            consultantMemberService.joinConsultant(consultantJoinDTO);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "member/consultJoin";
        }
        return "redirect:/";
    }
}
