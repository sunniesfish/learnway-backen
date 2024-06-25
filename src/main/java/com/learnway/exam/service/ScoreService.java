package com.learnway.exam.service;

import com.learnway.exam.domain.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ScoreService {

    public Page<Score> getScoreListByExam(Integer examId, Integer memId, Pageable pageable);
    public Optional<Score> getScoreById(Integer scoreId, Integer memId);
    public void writeScore(Score score);
    public Optional<Score> updateScore(Score score);
    public void deleteScore(Integer memId, Integer scoreId);
}
