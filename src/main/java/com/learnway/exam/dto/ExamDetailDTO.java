package com.learnway.exam.dto;

import com.learnway.exam.domain.Score;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamDetailDTO {
    String examName;
    String examType;
    Date examDate;
    Page<Score> scoreList;
}
