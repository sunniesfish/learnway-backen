package com.learnway.exam.controller;

import com.learnway.exam.domain.Exam;
import com.learnway.exam.domain.Score;
import com.learnway.exam.service.ExamService;
import com.learnway.exam.service.ScoreService;
import com.learnway.global.domain.Subject;
import com.learnway.member.service.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/score")
public class ScoreRestController {

    private final ExamService examService;

    /*
    * 시험의 과목별 점수 목록 가져오기
    * */
    @GetMapping("/{examId}/{pageNo}")
    public ResponseEntity<Page<Score>> getScoreList(
            @PathVariable("pageNo") int pageNo,
            @PathVariable("examId") Long examId,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        //get memId
        Long memId = userDetails.getMemberId();
        System.out.println("memId = " + memId);
        System.out.println("exmId = " + examId);

        Page<Score> page = examService.getScoreListByExam(examId, memId, PageRequest.of(pageNo-1,10));
        page.get().forEach(System.out::println);
        if (memId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<Page<Score>>(page, HttpStatus.OK);
        }
    }
    
    /*
    * 과목 상세 불러오기
    * */
    @GetMapping("/exam/{scoreId}")
    public ResponseEntity<Score> getScore(@PathVariable long scoreId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        //get memId
        Long memId = userDetails.getMemberId();
        Optional<Score> score = examService.getScoreById(scoreId, memId);
        return score.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /*
    * 점수 등록
    * */
    @PostMapping("/{examId}")
    public ResponseEntity<Score> createScore(
            @PathVariable("examId") Long examId,
            @ModelAttribute Score score,
            @RequestParam("subjectCode") String subjectCode,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        System.out.println("subjectCode = " + subjectCode);
        //get memId
        Long memId = userDetails.getMemberId();
        new Exam();
        score.setExam(Exam.builder().examId(examId).build());
        score.setSubject(Subject.builder().subjectCode(subjectCode).build());
        score.setMemId(memId);
        examService.writeScore(score);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /*
    * 점수 수정
    * */
    @PutMapping("/{examId}/{scoreId}")
    public ResponseEntity<Score> updateScore(
            @PathVariable("examId") Long examId,
            @PathVariable("scoreId") Integer scoreId,
            @ModelAttribute Score score,
            @RequestParam("subjectCode") String subjectCode,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        //get memId
        Long memId = userDetails.getMemberId();
        System.out.println("memId = " + memId);
        System.out.println("subjectCode = " + subjectCode);
        score.setExam(Exam.builder().examId(examId).build());
        score.setSubject(Subject.builder().subjectCode(subjectCode).build());
        score.setMemId(memId);
        Optional<Score> opScore = examService.updateScore(score);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /*
    * 점수 삭제
    * */
    @DeleteMapping("/{examId}/{scoreId}")
    public ResponseEntity<Score> deleteScore(
            @PathVariable("examId") Long examId,
            @PathVariable("scoreId") Long scoreId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        //get memId
        Long memId = userDetails.getMemberId();
        System.out.println("memId = " + memId);

        if (scoreId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            examService.deleteScore(memId, scoreId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

}
