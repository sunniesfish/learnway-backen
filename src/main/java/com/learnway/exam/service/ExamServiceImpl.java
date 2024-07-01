package com.learnway.exam.service;

import com.learnway.exam.domain.Exam;
import com.learnway.exam.domain.ExamRepository;
import com.learnway.exam.domain.Score;
import com.learnway.exam.dto.ExamDetailDTO;
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
    private final ScoreService scoreService;

    //시험 등록
    @Override
    public void writeExam(Exam exam) {
        examRepository.save(exam);
    }

    //시험 목록 불러오기
    @Override
    public Page<Exam> readExam(Long memId, int pageNo, int pageSize) {
        return examRepository.findByMemIdOrderByExamDateDesc(memId, PageRequest.of(pageNo -1, pageSize));
    }

    //시험 내용 수정
    @Override
    @Transactional
    public void updateExam(Exam exam) {
        Optional<Exam> opExam = examRepository.findByMemIdAndExamId(exam.getMemId(), exam.getExamId());
        opExam.ifPresent(value -> {
            value.setExamName(exam.getExamName());
            value.setExamType(exam.getExamType());
            value.setExamRange(exam.getExamRange());
            value.setExamDate(exam.getExamDate());
            value.setExamMemo(exam.getExamMemo());
            examRepository.save(value);
        });
    }

    //시험 삭제
    @Override
    public void deleteExam(Long examId, Long memId) {
        examRepository.deleteByMemIdAndExamId(memId, examId);
    }

    //시험 상세 정보
    @Override
    public Optional<Exam> findExamById(Long examId, Long memId) {
        return examRepository.findByMemIdAndExamId(memId, examId);
    }

    //시험 상세 페이지 가져오기
    @Transactional
    @Override
    public ExamDetailDTO getExamDetail(Long examId, Long memId) {
        ExamDetailDTO dto = new ExamDetailDTO();
        if (memId != null) {
            Optional<Exam> opExam = findExamById(examId, memId);
            if (opExam.isPresent()) {
                Exam exam = opExam.get();
                Page<Score> scores = scoreService.getScoreListByExam(examId, memId, PageRequest.of(1,10));
                dto.setExamName(exam.getExamName());
                dto.setExamType(exam.getExamType());
                dto.setExamDate(exam.getExamDate());
                dto.setScoreList(scores);
                return dto;
            } else return null;
        } else return null;
    }
}
