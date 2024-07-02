package com.learnway.exam.service;

import com.learnway.exam.domain.Exam;
import com.learnway.exam.domain.ExamRepository;
import com.learnway.exam.domain.Score;
import com.learnway.exam.domain.ScoreRepository;
import com.learnway.exam.dto.ExamDetailDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ExamServiceImpl implements ExamService{

    private final ExamRepository examRepository;
    private final ScoreRepository scoreRepository;
//    private final ScoreService scoreService;

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
    @Transactional
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
                Page<Score> scores = getScoreListByExam(examId, memId, PageRequest.of(1,10));
                dto.setExamId(exam.getExamId());
                dto.setExamName(exam.getExamName());
                dto.setExamType(exam.getExamType());
                dto.setExamDate(exam.getExamDate());
                dto.setExamRange(exam.getExamRange());
                dto.setExamMemo(exam.getExamMemo());
                dto.setScoreList(scores);
                return dto;
            } else return null;
        } else return null;
    }

    @Override
    public List<Exam> getExamsByExamType(Long memId, String examType) {
        return examRepository.findByMemIdAndExamType(memId, examType);
    }

    /*
     * 시험id로 과목별 점수 페이지 가져옴
     * */
    public Page<Score> getScoreListByExam(Long examId, Long memId, Pageable pageable) {
        return scoreRepository.findByMemIdAndExam_ExamId(memId, examId, pageable);
    }

    //memId로 점수 페이지 가져오기
    @Override
    public Page<Score> getScoresByMemId(Long memId, Pageable pageable) {
        return scoreRepository.findByMemId(memId, pageable);
    }

    //memId로 점수 리스트 가져오기
    @Override
    public List<Score> getScoreListByMemId(Long memId) {
        return scoreRepository.findAllByMemId(memId);
    }

    /*
     * 점수 상세
     * */
    @Override
    public Optional<Score> getScoreById(Long scoreId, Long memId) {
        return scoreRepository.findByMemIdAndScoreId(memId, scoreId);
    }

    /*
     * 점수 입력
     * */
    @Override
    @Transactional
    public void writeScore(Score score) {
        scoreRepository.save(score);
    }

    /*
     * 점수 수정
     * */
    @Override
    @Transactional
    public Optional<Score> updateScore(Score score) {
        Optional<Score> opScore = scoreRepository.findByMemIdAndScoreId(score.getMemId(), score.getScoreId());
        opScore.ifPresent(value -> {
            value.setScoreExScore(score.getScoreExScore());
            value.setScoreScore(score.getScoreScore());
            value.setScoreGrade(score.getScoreGrade());
            value.setScoreExScore(score.getScoreExScore());
            value.setScoreStdScore(score.getScoreStdScore());
            value.setSubject(score.getSubject());
            value.setScoreMemo(score.getScoreMemo());
            scoreRepository.save(value);
        });
        return opScore;
    }

    /*
     * 점수 삭제
     * */
    @Override
    @Transactional
    public void deleteScore(Long memId ,Long scoreId) {
        scoreRepository.deleteByMemIdAndScoreId(memId, scoreId);
    }

    @Override
    public List<Score> getScoreListBySubjectCode(Long memId, String subjectCode) {
        return List.of();
    }

    @Override
    public List<Score> getGrades(Long memId) {
        return List.of();
    }

    /*
     * 시험 유형별 점수 목록
     * */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public List<Score> getScoreListByExamType(Long memId, String examType) {
        List<Exam> list = null;
        List<Score> scores = null;
        list = getExamsByExamType(memId, examType);
        if(!list.isEmpty()){
            list.stream()
                    .forEach(exam -> {
                        Optional<Score> score = scoreRepository.findByMemIdAndExam_ExamId(memId, exam.getExamId());
                        if(score.isPresent()){scores.add(score.get());}
                    });
        }
        return scores;
    }

    @Override
    public List<Integer> getAvgScores(Long memId) {
        return List.of();
    }
}
