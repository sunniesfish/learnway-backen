package com.learnway.exam.controller;

import com.learnway.exam.domain.Exam;
import com.learnway.exam.service.ExamService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/exam")
public class ExamRestController {

    private final ExamService examService;

    
    /*
    * 시험 상세 정보 가져오기
    * */
    @GetMapping("/{examId}")
    public ResponseEntity<Exam> getExam(@PathVariable Integer examId){

        //getId
        int memId = 1;

        Exam exam = examService.findExamById(examId, memId);
        if (exam != null) {
            return new ResponseEntity<>(exam, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
