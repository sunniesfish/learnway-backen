package com.learnway.exam.domain;

import com.learnway.global.domain.Subject;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Score")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id", nullable = false)
    private Long scoreId;

    @Column(name = "member_id", nullable = false)
    private Long memId;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "subject_code")
    private Subject subject;

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
    public Score(Long scoreId, Exam exam, Subject subject, int scoreExScore, int scoreScore, int scoreGrade, int scoreStdScore, String scoreMemo) {
        this.scoreId = scoreId;
        this.exam = exam;
        this.subject = subject;
        this.scoreExScore = scoreExScore;
        this.scoreScore = scoreScore;
        this.scoreGrade = scoreGrade;
        this.scoreStdScore = scoreStdScore;
        this.scoreMemo = scoreMemo;
    }
}
