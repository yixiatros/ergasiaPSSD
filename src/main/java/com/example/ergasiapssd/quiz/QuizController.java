package com.example.ergasiapssd.quiz;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "quizzes")
public class QuizController {

    public final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping(path = "/myQuizzes/{offset}/{pageSize}")
    public String showMyQuizzes(Model model,
                                @PathVariable int offset,
                                @PathVariable int pageSize) {

        Page<Quiz> page = quizService.showMyQuizzes(offset, pageSize);
        List<Quiz> myQuizzes = page.getContent();
        model.addAttribute("myQuizzes", myQuizzes);
        model.addAttribute("page", page);
        model.addAttribute("offset", offset);
        model.addAttribute("pageSize", pageSize);

        return "quiz_show_my";
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

    @GetMapping(path = "/{quizId}/delete")
    public RedirectView delete(@PathVariable("quizId") Long id) {
        quizService.deleteQuiz(id);

        return new RedirectView("/quizzes/myQuizzes/0/20");
    }
}
