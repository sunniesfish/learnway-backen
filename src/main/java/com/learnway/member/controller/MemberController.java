package com.learnway.member.controller;

import com.learnway.member.dto.JoinDTO;
import com.learnway.member.dto.MemberUpdateDTO;
import com.learnway.member.dto.TargetUniDTO;
import com.learnway.member.service.CustomUserDetails;
import com.learnway.member.service.EmailService;
import com.learnway.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService; // 이메일 검증 확인 로직

    // Get 요청 시 회원가입 폼 리턴
    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("joinDTO", new JoinDTO());
        return "member/join";
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
            return "member/join";
        }
        // Password / ConfirmPassword 일치 여부 확인
        if (!joinDTO.getPassword().equals(joinDTO.getConfirmPassword())) {
            model.addAttribute("passwordError", "비밀번호가 일치하지 않습니다.");
            return "member/join";
        }
        memberService.joinMember(joinDTO);
        return "redirect:/member/joinSuccess"; // 회원 가입 성공 시 joinSuccess 에서 3초 후 로그인 폼으로 이동
    }

    // 이메일 인증 팝업
    @GetMapping("/emailVerification")
    public String emailVerification() {
        return "member/emailVerification";
    }
    // 회원 가입 성공 페이지
    @GetMapping("/joinSuccess")
    public String joinSuccess() {
        return "member/joinSuccess";
    }


    @GetMapping("/update")
    public String updateForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        MemberUpdateDTO memberUpdateDTO = memberService.getMemberInfo(userDetails.getUsername());

        // 목표대학은 3개
        while (memberUpdateDTO.getTargetUnis().size() < 3) {
            memberUpdateDTO.getTargetUnis().add(new TargetUniDTO());
        }

        model.addAttribute("memberUpdateDTO", memberUpdateDTO);
        return "member/update";
    }

    // 회원 정보 수정 처리
    @PostMapping("/update")
    public String update(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @Valid @ModelAttribute MemberUpdateDTO memberUpdateDTO,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            }
            return "member/update";
        }

        try {
            memberService.updateMemberInfo(userDetails.getUsername(), memberUpdateDTO);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "member/update";
        }

        return "redirect:/loginOk"; // 수정 성공 시 loginOk 페이지로 리다이렉트
    }
}
