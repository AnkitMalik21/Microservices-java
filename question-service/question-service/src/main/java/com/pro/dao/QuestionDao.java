package com.pro.dao;

import com.pro.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionDao extends JpaRepository<Question,Integer> {
    public List<Question> findByCategory(String category);

    @Query(value = "SELECT q.id from question q where q.category=:category ORDER BY RANDOM() LIMIT :numQ",
            nativeQuery = true)
    List<Integer> findRandomQuestionByCategory(String category,Integer numQ);
}
