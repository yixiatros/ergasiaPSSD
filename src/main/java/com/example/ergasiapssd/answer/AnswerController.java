package com.example.ergasiapssd.answer;

import com.example.ergasiapssd.quiz.Quiz;
import com.example.ergasiapssd.quiz.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping(path = "answers")
public class AnswerController {

    public final AnswerService answerService;
    public final QuizService quizService;

    @Autowired
    public AnswerController(AnswerService answerService,
                            QuizService quizService) {
        this.answerService = answerService;
        this.quizService = quizService;
    }

    @GetMapping(path = "/{quizId}/{answerId}")
    public String review(@PathVariable("quizId") UUID quizId,
                         @PathVariable("answerId") Long answerId,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        Optional<Answer> answerOptional = answerService.getAnswerById(answerId);
        Optional<Quiz> quizOptional = quizService.getById(quizId);
        if (answerOptional.isEmpty() || quizOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("danger", "Answer to the quiz was not found.");
            return "redirect:/index";
        }

        model.addAttribute("answer", answerOptional.get());
        model.addAttribute("quiz", quizOptional.get());

        return "answer/review";
    }
}
