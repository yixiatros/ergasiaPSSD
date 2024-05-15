package com.example.ergasiapssd.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

    public final QuizRepository quizRepository;
    public final QuestionRepository questionRepository;
    public final MultipleChoiceRepository multipleChoiceRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository, MultipleChoiceRepository multipleChoiceRepository){
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.multipleChoiceRepository = multipleChoiceRepository;
    }
}
