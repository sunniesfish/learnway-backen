package com.learnway.exam.controller;

import com.learnway.exam.service.ExamService;
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

    private final ExamService examService;

    //과목별 성적
    @GetMapping("/subject/{subjectCode}")
    public ResponseEntity getScoreBySubject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("subjectCode") String subjectCode
    ) {
        Long memId = userDetails.getMemberId();

        if (memId == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else {
            List list = new ArrayList<>();
            list = examService.getScoreListByExamType(memId, subjectCode);
            return new ResponseEntity(list, HttpStatus.OK);
        }
    }

    //전체 성적 추이
    @GetMapping("/all")
    public ResponseEntity getScoreAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memId = userDetails.getMemberId();
        if (memId == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else {
            List list = new ArrayList<>();
            list = examService.getScoreListByMemId(memId);
            return new ResponseEntity<>(list,HttpStatus.OK);
        }
    }

    //시험 유형별 성적 추이
    @GetMapping("/type/{examType}")
    public ResponseEntity getScoreByExamType(
            @PathVariable String examType,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long memId = userDetails.getMemberId();
        if (memId == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else {
            List list = new ArrayList<>();
            list = examService.getScoreListByExamType(memId, examType);
            return new ResponseEntity(list,HttpStatus.OK);
        }
    }
}
