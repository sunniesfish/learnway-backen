package com.learnway.study.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyProblemRepository extends JpaRepository<StudyProblem, Long> {
	
	StudyProblem findByStudyPostid(int postid);
}
