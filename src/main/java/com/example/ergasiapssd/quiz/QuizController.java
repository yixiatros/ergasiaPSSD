package com.example.ergasiapssd.quiz;

import com.example.ergasiapssd.answer.Answer;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    @GetMapping(path = "/myQuizzes/{quizId}/answers/{offset}/{pageSize}")
    public String answersOfQuizWithId(Model model,
                                      @PathVariable("quizId") UUID id,
                                      @PathVariable("offset") int offset,
                                      @PathVariable("pageSize") int pageSize) {

        Optional<Quiz> quizOptional = quizService.getById(id);
        Page<Answer> page = quizService.answersOfQuizWithId(id, offset, pageSize);
        List<Answer> answers = page.getContent();
        quizOptional.ifPresent(quiz -> model.addAttribute("quiz", quiz));
        model.addAttribute("answers", answers);
        model.addAttribute("page", page);
        model.addAttribute("offset", offset);
        model.addAttribute("pageSize", pageSize);

        return "quiz/quiz_answers";
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
    public RedirectView delete(@PathVariable("quizId") UUID id) {
        quizService.deleteQuiz(id);

        return new RedirectView("/quizzes/myQuizzes/0/20");
    }

    @GetMapping(path = "/{quizId}/edit")
    public String editView(@PathVariable("quizId") UUID id, Model model) {
        Optional<Quiz> quizOptional = quizService.getById(id);

        if (quizService.editView(id) || quizOptional.isEmpty())
            return "redirect:/quizzes/myQuizzes/0/20";

        model.addAttribute("quiz", quizOptional.get());

        return "quiz/edit";
    }

    @GetMapping(path = "/{quizId}/editing")
    public RedirectView edit(@PathVariable("quizId") UUID id, @RequestParam Map<String,String> allRequestParams) {

        quizService.edit(id, allRequestParams);

        return new RedirectView("/quizzes/myQuizzes/0/20");
    }

    @GetMapping(path = "/solve/{quizId}")
    public String solve(Model model, @PathVariable("quizId") UUID id) {
        Optional<Quiz> quizOptional = quizService.getById(id);

        if (quizOptional.isEmpty()){
            return "error/404";
        }

        model.addAttribute("quiz", quizOptional.get());

        List<Answer> answers = quizService.getAnswersOfUserToQuiz(
                SecurityContextHolder.getContext().getAuthentication().getName(), id);

        model.addAttribute("answers", answers);

        return "quiz/solve";
    }

    @PostMapping(path = "/solve/{quizId}")
    public RedirectView submitSolve(@RequestParam Map<String,String> allRequestParams,
                                    @PathVariable("quizId") UUID id,
                                    RedirectAttributes redirectAttributes) {

        Pair<Integer, Integer> results = quizService.submitSolve(allRequestParams, id);

        Optional<Quiz> quizOptional = quizService.getById(id);

        if (results.a == -1 || results.b == -1 || quizOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong");
            return new RedirectView("/index");
        }

        redirectAttributes.addFlashAttribute("results", results);
        redirectAttributes.addFlashAttribute("quiz", quizOptional.get());

        return new RedirectView("/quizzes/solve/score");
    }

    @GetMapping(path = "/solve/score")
    public String result(Model model) {
        return "quiz/result";
    }

    @GetMapping(path = "/code")
    public RedirectView codeEntered(@RequestParam("code") String code,
                                    RedirectAttributes redirectAttributes) {
        if (!quizService.checkCode(code)){
            redirectAttributes.addFlashAttribute("invalidCode", "Invalid Code.");
            return new RedirectView("/index#code-popup");
        }
        return new RedirectView("/quizzes/solve/" + code);
    }

    @GetMapping(path = "/myQuizzes/{quizId}/lock/{offset}/{pageSize}")
    public RedirectView lockUnlock(@PathVariable("quizId") UUID quizId,
                                   @PathVariable("offset") int offset,
                                   @PathVariable("pageSize") int pageSize) {

        quizService.lockUnlock(quizId);

        return new RedirectView("/quizzes/myQuizzes/" + offset + "/" + pageSize);
    }

    @GetMapping(path = "/myQuizzes/{quizId}/share")
    public String shareQuiz(@PathVariable("quizId") UUID quizId,
                            @RequestParam(name = "search", defaultValue = "") String search,
                            Model model) {

        model.addAttribute("students", quizService.getUsersByRole("ROLE_STUDENT", search));
        model.addAttribute("quizId", quizId);

        return "quiz/share";
    }

    @GetMapping(path = "/myQuizzes/{quizId}/share/{userId}")
    public RedirectView shareQuizUser(@PathVariable("quizId") UUID quizId,
                                      @PathVariable("userId") Long userId) {

        quizService.shareQuiz(quizId, userId);

        return new RedirectView("/quizzes/myQuizzes/" + quizId + "/share");
    }

    @GetMapping(path = "/availableQuizzes")
    public String availableQuizzes(Model model) {

        model.addAttribute("availableQuizzes", quizService.getAvailableQuizzes());

        return "quiz/available_quizzes";
    }
}
