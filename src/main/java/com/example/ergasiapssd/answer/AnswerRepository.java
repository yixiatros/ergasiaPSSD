package com.example.ergasiapssd.answer;

import com.example.ergasiapssd.quiz.Quiz;
import org.aspectj.weaver.ast.Var;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("SELECT q.answers FROM Quiz q WHERE q.id = ?1")
    Page<Answer> findAnswersByQuizId (UUID quizId, Pageable pageable);

    @Query("SELECT a FROM Answer a, Quiz q WHERE a.user.id = ?1 AND q.id = ?2 AND (a member q.answers)")
    List<Answer> getAnswersOfUserToQuizByIds(Long userId, UUID quizId);
}
