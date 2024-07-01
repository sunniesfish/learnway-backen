package com.learnway.exam.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    Page<Score> findByMemIdAndExamId(Long memId, Long examId, Pageable pageable);
    Page<Score> findByMemId(Long memId, Pageable pageable);
    Optional<Score> findByMemIdAndScoreId(Long memId, Long scoreId);
    void deleteByMemIdAndExamId(Long memId, Long scoreId);
    Optional<Score> findByMemIdAndExamId(Long memId, Long examId);
    List<Score> findAllByMemId(Long memId);
}
