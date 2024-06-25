package com.learnway.exam.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {
    Page<Exam> findByMemIdOrderByExamDateDesc(Integer memId, Pageable pageable);

    void deleteByMemIdAndExamId(Integer memId, Integer examId);
    Optional<Exam> findByMemIdAndExamId(Integer memId, Integer examId);
}
