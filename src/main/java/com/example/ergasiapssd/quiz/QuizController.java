package com.example.ergasiapssd.quiz;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

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

    @GetMapping(path = "/create/new")
    public RedirectView createNewQuiz(@RequestParam Map<String,String> allRequestParams) {

        quizService.createQuiz(allRequestParams);

        return new RedirectView("/index");
    }
}
