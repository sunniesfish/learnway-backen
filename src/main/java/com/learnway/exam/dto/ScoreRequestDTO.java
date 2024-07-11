package com.learnway.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreRequestDTO {
    long examId;
    String subjectCode;
    int scoreScore;
    int scoreExScore;
    int scoreStdScore;
    int scoreGrade;
}
