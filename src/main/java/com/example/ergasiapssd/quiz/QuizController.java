package com.example.ergasiapssd.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        return "quiz/quiz_show_my";
    }

    @GetMapping(path = "/create")
    public String create(Model model) {

        model.addAttribute("newQuiz", new Quiz());

        return "quiz/create";
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

    @GetMapping(path = "/solve/{quizId}")
    public String solve(Model model, @PathVariable("quizId") Long id) {
        Optional<Quiz> quizOptional = quizService.getById(id);

        if (quizOptional.isEmpty()){
            return "error/404";
        }

        model.addAttribute("quiz", quizOptional.get());

        return "quiz/solve";
    }

    @PostMapping(path = "/solve/{quizId}")
    public RedirectView submitSolve(@RequestParam Map<String,String> allRequestParams,
                                    @PathVariable("quizId") Long id,
                                    RedirectAttributes redirectAttributes) {

        int score = quizService.submitSolve(allRequestParams, id);

        if (score == -1) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong");
            return new RedirectView("/index");
        }

        redirectAttributes.addFlashAttribute("score", score);

        return new RedirectView("/quizzes/solve/score");
    }

    @GetMapping(path = "/solve/score")
    public String result(Model model) {
        return "quiz/result";
    }
}
