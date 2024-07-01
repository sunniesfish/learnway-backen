package com.learnway.exam.service;

import com.learnway.exam.domain.Exam;
import com.learnway.exam.dto.ExamDetailDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ExamService {

    public void writeExam(Exam exam);
    public Page<Exam> readExam(Long memId, int pageNo, int pageSize);
    public void updateExam(Exam exam);
    public void deleteExam(Long examId, Long memId);
    public Optional<Exam> findExamById(Long examId, Long memId);
    public ExamDetailDTO getExamDetail(Long examId, Long memId);
}
