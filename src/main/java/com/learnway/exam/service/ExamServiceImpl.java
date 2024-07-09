package com.learnway.exam.service;

import com.learnway.exam.domain.Exam;
import com.learnway.exam.domain.ExamRepository;
import com.learnway.exam.domain.Score;
import com.learnway.exam.domain.ScoreRepository;
import com.learnway.exam.dto.ExamDetailDTO;
import com.learnway.global.domain.ExamType;
import com.learnway.global.domain.ExamTypeRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
public class ExamServiceImpl implements ExamService{

    private final ExamTypeRepository examTypeRepository;
    private final ExamRepository examRepository;
    private final ScoreRepository scoreRepository;
//    private final ScoreService scoreService;

    //시험 등록
    @Override
    @Transactional
    public void writeExam(Exam exam) {
        // Save ExamType first if it is not already persisted
        ExamType examType = exam.getExamType();
        if (examType.getExamTypeId() == null) { // Assuming ExamType has an ID field to check if it's persisted
            Optional<ExamType> examTypeOptional = examTypeRepository.findByExamTypeName(examType.getExamTypeName());
            if (examTypeOptional.isPresent()) {
                exam.setExamType(examTypeOptional.get());
            } else {
                exam.setExamType(examTypeRepository.save(examType));
            }
        }

        // Now save the Exam
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
                dto.setExamType(
                        ExamType.builder()
                                .examTypeName(exam.getExamType().getExamTypeName()).build()
                );
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
        return examRepository.findByMemIdAndExamType_ExamTypeName(memId, examType);
    }

    @Override
    public Page<Exam> getExamsByExamType(Long memId, String examTypeName, Pageable pageable) {
        return examRepository.findByMemIdAndExamType_ExamTypeNameOrderByExamDateDesc(memId, examTypeName, pageable);
    }

    @Override
    @Transactional
    public Page<Exam> findScoreListByExamType(Long memId, String examTypeName, Pageable pageable) {
        Page<Exam> page = getExamsByExamType(memId, examTypeName, pageable);
        page.get().forEach(exam -> {
            exam.setScoreList(
                    scoreRepository.findAllByMemIdAndExam_ExamId(memId, exam.getExamId())
            );
        });
        return page;
    }

    @Override
    public Page<Exam> findScoreList(Long memId, Pageable pageable) {
        Page<Exam> page = examRepository.findByMemIdOrderByExamDateDesc(memId, pageable);
        page.get().forEach(exam -> {
            exam.setScoreList(scoreRepository.findAllByMemIdAndExam_ExamId(memId, exam.getExamId()));
        });
        return page;
    }

    /*
     * 시험id로 과목별 점수 페이지 가져옴
     * */
    public Page<Score> getScoreListByExam(Long examId, Long memId, Pageable pageable) {
        return scoreRepository.findByMemIdAndExam_ExamIdOrderByExam_ExamDateDesc(memId, examId, pageable);
    }

    //memId로 점수 페이지 가져오기
    @Override
    public Page<Score> getScoresByMemId(Long memId, Pageable pageable) {
        return scoreRepository.findByMemIdOrderByExam_ExamDateDesc(memId, pageable);
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
    public Boolean writeScore(Score score, Long memId) {
        List<Score> optionalScore = scoreRepository.findAllByMemIdAndExam_ExamIdAndSubject_SubjectCode(memId, score.getExam().getExamId(), score.getSubject().getSubjectCode());
        if (optionalScore.isEmpty()) {
            scoreRepository.save(score);
            return true;
        } else {
            return false;
        }
    }

    /*
     * 점수 수정
     * */
    @Override
    @Transactional
    public Boolean updateScore(Score score, Long memId) {
        AtomicReference<Boolean> isSucceed = new AtomicReference<>(false);
        Optional<Score> opScore = scoreRepository.findByMemIdAndScoreId(score.getMemId(), score.getScoreId());
        opScore.ifPresent(value -> {
            value.setScoreExScore(score.getScoreExScore());
            value.setScoreScore(score.getScoreScore());
            value.setScoreGrade(score.getScoreGrade());
            value.setScoreStdScore(score.getScoreStdScore());
            value.setSubject(score.getSubject());
            value.setScoreMemo(score.getScoreMemo());

            List<Score> scoreList = scoreRepository.findAllByMemIdAndExam_ExamIdAndSubject_SubjectCode(
                    memId, score.getExam().getExamId(), score.getSubject().getSubjectCode()
            );

            // 과목이 중복되는 것을 방지
            if (scoreList.size() < 2) {
                scoreRepository.save(value);
                isSucceed.set(true);
            }

        });
        return isSucceed.get();
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
    @Transactional
    public Page<Score> getScoreListByExamType(Long memId, String examType, Pageable pageable) {
        List<Exam> list = null;
        Page<Score> scores = null;
        list = getExamsByExamType(memId, examType);
        if(!list.isEmpty()){
            scores = scoreRepository.findByMemIdAndExam_ExamIdIn (memId, list.stream().map(Exam::getExamId).toList(),pageable );
        }
        return scores;
    }

    @Override
    public List<Integer> getAvgScores(Long memId) {
        return List.of();
    }
}
