package com.learnway.exam.controller;

import com.learnway.exam.domain.Score;
import com.learnway.exam.service.ExamService;
import com.learnway.exam.service.ScoreService;
import com.learnway.member.service.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @GetMapping("/subject/{subjectCode}/{pageNo}")
    public ResponseEntity<Page<Score>> getScoreBySubject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("subjectCode") String subjectCode,
            @PathVariable("pageNo") int pageNo
    ) {
        Long memId = userDetails.getMemberId();

        if (memId == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else {
            Page page = examService.getScoreListByExamType(memId, subjectCode, PageRequest.of(pageNo-1, 10));
            return new ResponseEntity(page, HttpStatus.OK);
        }
    }


    //시험 유형별 성적 추이
    @GetMapping("/{examType}/{pageNo}")
    public ResponseEntity<Page<Score>> getScoreByExamType(
            @PathVariable String examType,
            @PathVariable int pageNo,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long memId = userDetails.getMemberId();
        if (memId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            Page<Score> page;
            if(!examType.equals("all")){
                page = examService.getScoreListByExamType(memId, examType,PageRequest.of(pageNo - 1, 8));
            } else {
                page = examService.getScoresByMemId(memId, PageRequest.of(pageNo - 1, 8));
            }
            return new ResponseEntity<>(page, HttpStatus.OK);
        }
    }
}
