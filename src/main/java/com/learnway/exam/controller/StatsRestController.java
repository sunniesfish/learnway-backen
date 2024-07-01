package com.learnway.exam.controller;

import com.learnway.exam.service.ScoreService;
import com.learnway.member.service.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/stats")
public class StatsRestController {

    private final ScoreService scoreService;

    //과목별 성적
    @GetMapping("/{subjectCode}")
    public ResponseEntity getScoreBySubject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("subjectCode") String subjectCode
    ) {
        Long memId = userDetails.getMemberId();

        if (memId == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else {
            List list = new ArrayList<>();
            list = scoreService.getScoreListByExamType(memId, subjectCode);
            return new ResponseEntity(list, HttpStatus.OK);
        }
    }
}
