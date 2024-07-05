package com.learnway.member.controller;

import com.learnway.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberRestController {

    private final MemberService memberService;
    private final Path uploadPath = Paths.get("C:/uploads"); // 외부 경로

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

    // 외부 경로 사용
    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = uploadPath.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}