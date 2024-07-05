package com.learnway.study.domain;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface StudyRepository extends JpaRepository<Study, Integer> {
	
	  @Query("SELECT s FROM Study s LEFT JOIN FETCH s.tags WHERE s.postid = :postid")
	    Optional<Study> findByIdWithTags(@Param("postid") Long postid);
	  
	  List<Study> findByTitle(String title);
}


