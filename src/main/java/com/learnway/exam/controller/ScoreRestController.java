package com.learnway.exam.controller;

import com.learnway.exam.domain.Score;
import com.learnway.exam.service.ScoreService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/score")
public class ScoreRestController {

    private final ScoreService scoreService;

    /*
    * 시험의 과목별 점수 목록 가져오기
    * */
    @GetMapping("/{examId}/{pageNo}")
    public ResponseEntity<Page<Score>> getScoreList(
            @PathVariable("pageNo") int pageNo,
            @PathVariable("examId") Integer examId
    ) {
        //get memId
        Integer memId = 1;

        Page<Score> page = scoreService.getScoreListByExam(examId, memId, PageRequest.of(pageNo,10));
        if (memId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<Page<Score>>(page, HttpStatus.OK);
        }
    }
    
    /*
    * 과목 상세 불러오기
    * */
    @GetMapping("/{scoreId}")
    public ResponseEntity<Score> getScore(@PathVariable int scoreId) {
        //get memId
        Integer memId = 1;
        Optional<Score> score = scoreService.getScoreById(scoreId, memId);

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
            @PathVariable Integer examId,
            @RequestBody Score score
    ) {
        //get memId
        Integer memId = 1;

        score.setMemId(memId);
        if (score == null || memId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        scoreService.writeScore(score);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /*
    * 점수 수정
    * */
    @PutMapping("/{examId}/{scoreId}")
    public ResponseEntity<Score> updateScore(
            @PathVariable("examId") Integer examId,
            @PathVariable("scoreId") Integer scoreId,
            @RequestBody Score score
    ){
        //get memId
        Integer memId = 1;

        score.setMemId(memId);
        Optional<Score> opScore = scoreService.updateScore(score);

        if (opScore.isEmpty() || memId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /*
    * 점수 삭제
    * */
    @DeleteMapping("/{examId}/")
    public ResponseEntity<Score> deleteScore(
            @PathVariable("examId") Integer examId,
            @PathVariable("scoreId") Integer scoreId
    ) {
        //get memId
        Integer memId = 1;

        if (scoreId == null || memId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            scoreService.deleteScore(memId, scoreId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
