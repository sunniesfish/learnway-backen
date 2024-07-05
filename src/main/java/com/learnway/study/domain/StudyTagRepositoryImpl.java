package com.learnway.study.domain;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


import java.util.ArrayList;
import java.util.List;


public class StudyTagRepositoryImpl implements StudyTagRepositoryCustom {

    @PersistenceContext
    private  EntityManager entityManager;

    @Override
    public List<StudyTag> findByTag(List<String> tags) {
    	System.out.println("--1--");
    	
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        System.out.println("--2--");
        CriteriaQuery<StudyTag> query = cb.createQuery(StudyTag.class);
        System.out.println("--3--");
        Root<StudyTag> root = query.from(StudyTag.class);
        System.out.println("--4--");

        List<Predicate> predicates = new ArrayList<>();
        for (String tag : tags) {
            predicates.add(cb.like(root.get("tag"), "%" + tag + "%"));
        }

        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getResultList();
    }
}
