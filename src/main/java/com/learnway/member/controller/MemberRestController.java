package com.learnway.member.controller;

import com.learnway.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberRestController {

    private final MemberService memberService;

    // ID 중복 체크
    @GetMapping("/check-id")
    public ResponseEntity<String> checkId(@RequestParam("username") String username) {
        if (username.length() < 4 || username.length() > 20) {
            return ResponseEntity.badRequest().body("아이디는 4자 이상, 20자 이하로 입력해주세요.");
        }

        if (memberService.isUsernameTaken(username)) {
            return ResponseEntity.ok("이미 사용 중인 ID입니다.");
        }

        return ResponseEntity.ok("사용 가능한 ID입니다.");
    }
}