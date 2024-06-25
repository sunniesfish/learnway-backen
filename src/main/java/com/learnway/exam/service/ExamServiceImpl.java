package com.learnway.exam.service;

import com.learnway.exam.domain.Exam;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ExamServiceImpl implements ExamService{

    @Override
    public Exam writeExam(Exam exam) {
        return null;
    }

    @Override
    public Page<Exam> readExam(Integer memId) {
        return null;
    }

    @Override
    public Exam updateExam(Exam exam) {
        return null;
    }

    @Override
    public void deleteExam(Integer examId, Integer memId) {

    }

    @Override
    public Exam findExamById(Integer examId, Integer memId) {
        return null;
    }
}
