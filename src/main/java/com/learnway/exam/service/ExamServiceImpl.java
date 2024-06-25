package com.learnway.exam.service;

import com.learnway.exam.domain.Exam;
import com.learnway.exam.domain.ExamRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ExamServiceImpl implements ExamService{

    private final ExamRepository examRepository;

    //시험 등록
    @Override
    public void writeExam(Exam exam) {
        examRepository.save(exam);
    }

    //시험 목록 불러오기
    @Override
    public Page<Exam> readExam(Integer memId, int pageNo, int pageSize) {
        return examRepository.findbyMemId(memId, PageRequest.of(pageNo -1, pageSize));
    }

    //시험 내용 수정
    @Override
    @Transactional
    public void updateExam(Exam exam) {
        Optional<Exam> opExam = examRepository.findByMemIdAndExamId(exam.getMemId(),exam.getExamId());
        opExam.ifPresent(value -> value.setExamName(exam.getExamName()));
        opExam.ifPresent(value -> value.setExamType(exam.getExamType()));
        opExam.ifPresent(value -> value.setExamRange(exam.getExamRange()));
        opExam.ifPresent(value -> value.setExamDate(exam.getExamDate()));
        opExam.ifPresent(value -> value.setExamMemo(exam.getExamMemo()));
    }

    //시험 삭제
    @Override
    public void deleteExam(Integer examId, Integer memId) {
        examRepository.deleteByMemIdAndExamId(memId, examId);
    }

    //시험 상세 정보
    @Override
    public Exam findExamById(Integer examId, Integer memId) {
        Optional exam = examRepository.findByMemIdAndExamId(memId, examId);
        if (exam.isPresent()){
            return (Exam) exam.get();
        }
        return null;
    }
}
