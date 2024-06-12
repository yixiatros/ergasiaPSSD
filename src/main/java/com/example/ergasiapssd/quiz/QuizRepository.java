package com.example.ergasiapssd.quiz;

import com.example.ergasiapssd.answer.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {

    @Query("SELECT q FROM Quiz q WHERE q.creator.username = ?1")
    Page<Quiz> findQuizzesByUsername (String username, Pageable pageable);

    @Query("SELECT q FROM Quiz q, User u WHERE u.id = ?1 AND (u MEMBER q.visibleUsers)")
    List<Quiz> findAllAvailableQuizzesOfUserById (Long userId);
}
