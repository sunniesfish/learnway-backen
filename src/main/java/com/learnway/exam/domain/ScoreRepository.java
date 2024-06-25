package com.learnway.exam.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Integer> {

    Page<Score> findByMemIdAndExamId(Integer memId, Integer examId, Pageable pageable);
    Optional<Score> findByMemIdAndScoreId(Integer memId, Integer scoreId);
    void deleteByMemIdAndExamId(Integer memId, Integer scoreId);
}
