package com.example.ergasiapssd.answer;

import com.example.ergasiapssd.quiz.Quiz;
import org.aspectj.weaver.ast.Var;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("SELECT q.answers FROM Quiz q WHERE q.id = ?1")
    Page<Answer> findAnswersByQuizId (UUID quizId, Pageable pageable);

}
