package com.example.ergasiapssd.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "quizzes")
public class QuizController {

    public final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping(path = "/create")
    public String create(Model model) {

        model.addAttribute("newQuiz", new Quiz());

        return "quiz_create";
    }
}
