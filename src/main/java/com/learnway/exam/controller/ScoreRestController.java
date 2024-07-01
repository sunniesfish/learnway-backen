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

        Page<Score> page = examService.getScoreListByExam(examId, memId, PageRequest.of(pageNo,10));
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
    public ResponseEntity<Score> getScore(@PathVariable long scoreId) {
        //get memId
        Long memId = 1l;
        Optional<Score> score = examService.getScoreById(scoreId, memId);

        if (!score.isPresent() || memId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(score.get(), HttpStatus.OK);
        }
    }

    /*
    * 점수 등록
    * */
    @PostMapping("/{examId}")
    public ResponseEntity<Score> createScore(
            @PathVariable Long examId,
            @RequestBody Score score,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        //get memId
        Long memId = userDetails.getMemberId();

        score.setMemId(memId);
        if (score == null || memId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        examService.writeScore(score);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /*
    * 점수 수정
    * */
    @PutMapping("/{examId}/{scoreId}")
    public ResponseEntity<Score> updateScore(
            @PathVariable("examId") Integer examId,
            @PathVariable("scoreId") Integer scoreId,
            @RequestBody Score score,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        //get memId
        Long memId = userDetails.getMemberId();

        score.setMemId(memId);
        Optional<Score> opScore = examService.updateScore(score);

        if (opScore.isEmpty() || memId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
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

        if (scoreId == null || memId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            examService.deleteScore(memId, scoreId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

}
