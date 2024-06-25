package com.learnway.exam.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "Score")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id", nullable = false)
    private Integer scoreId;

    @Column(name = "exam_id", nullable = false)
    private Integer examId;

    @Column(name = "subject_code", nullable = false)
    private String subjectCode;

    @Column(name = "score_ex_score", nullable = false, columnDefinition = "TINYINT DEFAULT 100")
    private int scoreExScore;

    @Column(name = "score_score", nullable = false)
    private int scoreScore;

    @Column(name = "score_grade", nullable = true)
    private int scoreGrade;

    @Column(name = "score_std_score", nullable = true)
    private int scoreStdScore;

    @Column(name = "score_memo", nullable = true)
    private String scoreMemo;

    @Builder
    public Score(Integer scoreId, Integer examId, String subjectCode, int scoreExScore, int scoreScore, int scoreGrade, int scoreStdScore, String scoreMemo) {
        this.scoreId = scoreId;
        this.examId = examId;
        this.subjectCode = subjectCode;
        this.scoreExScore = scoreExScore;
        this.scoreScore = scoreScore;
        this.scoreGrade = scoreGrade;
        this.scoreStdScore = scoreStdScore;
        this.scoreMemo = scoreMemo;
    }
}
