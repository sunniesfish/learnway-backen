package com.learnway.exam.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Page<Exam> findByMemIdOrderByExamDateDesc(Long memId, Pageable pageable);

    void deleteByMemIdAndExamId(Long memId, Long examId);
    Optional<Exam> findByMemIdAndExamId(Long memId, Long examId);
    List<Exam> findByMemIdAndExamType(Long memId, String examType);
}
