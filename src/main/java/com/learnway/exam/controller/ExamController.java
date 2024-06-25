package com.learnway.exam.controller;

import com.learnway.exam.domain.Exam;
import com.learnway.exam.service.ExamService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@Controller
@AllArgsConstructor
@RequestMapping("/exam")
public class ExamController {

    private final ExamService examService;

    /*
    * 시험등록 모달에서 시험 종류 및 이름 등록 하고 시험 리스트 페이지로 리다이렉트
    */
    @PostMapping("/register")
    public String registerExam(
            @RequestParam(name = "examName") String examName,
            @RequestParam(name = "examType") String examType,
            @RequestParam(name = "examRange") String examRange,
            @RequestParam(name = "examDate") Date examDate,
            @RequestParam(name = "examMemo") String examMemo
            ){
        // get memId
        int memId = 1;

        examService.writeExam(
                new Exam().builder()
                        .memId(memId)
                        .examName(examName)
                        .examType(examType)
                        .examRange(examRange)
                        .examDate(examDate)
                        .examMemo(examMemo).build()
        );
        return "redirect:/exam/list";
    }


    /**
     * 사용자의 시험 리스트 가져옴
     */
    @GetMapping("/list")
    public String getExamList(Model model){
        //get memId
        int memeId = 1;

        model.addAttribute("examList", examService.readExam(memeId));
        return "/template/exam";
    }

    /*
    *  시험 내용 수정
    * */
    @PostMapping("/update")
    public String updateExam(
            @RequestParam(name = "examName") String examName,
            @RequestParam(name = "examType") String examType,
            @RequestParam(name = "examRange") String examRange,
            @RequestParam(name = "examDate") Date examDate,
            @RequestParam(name = "examMemo") String examMemo
    ){
        // get memId
        int memId = 1;

        examService.updateExam(
                new Exam().builder()
                        .memId(memId)
                        .examName(examName)
                        .examType(examType)
                        .examRange(examRange)
                        .examDate(examDate)
                        .examMemo(examMemo).build()
        );
        return "redirect:/exam/list";
    }

    /*
    * 시험 삭제 후 시험리스트 페이지로 리다이렉트
    * */
    @GetMapping("/delete/{examId}")
    public String deleteExam(@PathVariable("examId") Integer examId){
        //get memId
        Integer memId = 1;
        if(memId != null) {
            examService.deleteExam(examId, memId);
        }
        return "redirect:/exam/list";
    }
}