package com.example.ergasiapssd.answer;

import com.example.ergasiapssd.quiz.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnswerService {

    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;

    @Autowired
    public AnswerService(QuizRepository quizRepository,
                         AnswerRepository answerRepository) {
        this.quizRepository = quizRepository;
        this.answerRepository = answerRepository;
    }

    public Optional<Answer> getAnswerById(Long answerId) {
        return answerRepository.findById(answerId);
    }
}
