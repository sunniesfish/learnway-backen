package com.learnway.study.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyProblemImgRepository extends JpaRepository<StudyProblemImg, Long> {

	List<StudyProblemImg> findByStudyProblemProblemid(int problemId);
}
