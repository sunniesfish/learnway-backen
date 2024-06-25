package com.learnway.exam.service;

import com.learnway.exam.domain.Exam;
import org.springframework.data.domain.Page;

public interface ExamService {

    public Exam writeExam(Exam exam);
    public Page<Exam> readExam(Integer memId);
    public Exam updateExam(Exam exam);
    public void deleteExam(Integer examId, Integer memId);
    public Exam findExamById(Integer examId, Integer memId);
}
