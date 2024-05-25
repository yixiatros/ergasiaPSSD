package com.example.ergasiapssd.quiz;

import com.example.ergasiapssd.user.User;
import com.example.ergasiapssd.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final MultipleChoiceRepository multipleChoiceRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository,
                       QuestionRepository questionRepository,
                       MultipleChoiceRepository multipleChoiceRepository,
                       UserRepository userRepository){
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.multipleChoiceRepository = multipleChoiceRepository;
        this.userRepository = userRepository;
    }

    public Optional<Quiz> getById(Long id) {
        return quizRepository.findById(id);
    }

    public Page<Quiz> showMyQuizzes(int offset, int pageSize) {
        Pageable sorted = PageRequest.of(offset, pageSize, Sort.by("title"));
        return quizRepository.findQuizzesByUsername(SecurityContextHolder.getContext().getAuthentication().getName(), sorted);
    }

    public void createQuiz(Map<String,String> allRequestParams) {

        Quiz newQuiz = new Quiz();

        Optional<User> userOptional = userRepository.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (userOptional.isEmpty())
            return;

        newQuiz.setCreator(userOptional.get());

        Question newQuestion = null;
        boolean isQuestionAddedToQuiz = false;

        for (var entry : allRequestParams.entrySet()) {
            if (entry.getKey().equals("title")){
                newQuiz.setTitle(entry.getValue());
            } else if (entry.getKey().startsWith("question")){
                isQuestionAddedToQuiz = false;
                newQuestion = new Question();
                newQuestion.setQuestion(entry.getValue());
            } else if (entry.getKey().startsWith("answer")) {
                MultipleChoice multipleChoice = new MultipleChoice();
                multipleChoice.setChoice(entry.getValue());
                if (newQuestion != null){
                    multipleChoiceRepository.save(multipleChoice);
                    newQuestion.addMultipleChoice(multipleChoice);
                    questionRepository.save(newQuestion);
                    if (!isQuestionAddedToQuiz){
                        newQuiz.addQuestion(newQuestion);
                        isQuestionAddedToQuiz = true;
                    }
                }
            } else if (entry.getKey().startsWith("radio")) {
                if (newQuestion != null)
                    newQuestion.setAnswer(Integer.parseInt(entry.getValue()));
            }
        }

        if (newQuiz.getQuestions().size() == 0)
            return;

        quizRepository.save(newQuiz);
    }

    public boolean deleteQuiz(Long id) {
        Optional<Quiz> quizOptional = quizRepository.findById(id);

        if(quizOptional.isEmpty())
            throw new IllegalStateException("Quiz with id " + id + "does not exist.");

        if (!quizOptional.get().getCreator().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName()) &&
                quizOptional.get().getCreator().getRoles().contains("ROLE_ADMIN"))
            return false;

        quizRepository.deleteById(id);
        return true;
    }

    public int submitSolve(Map<String,String> allRequestParams, Long id) {
        Optional<Quiz> quizOptional = quizRepository.findById(id);

        if (quizOptional.isEmpty())
            return -1;

        Quiz quiz = quizOptional.get();

        int score = 0;
        int i = 0;

        for (var entry : allRequestParams.entrySet()) {
            String[] strings = entry.getValue().split(",");
            if (Integer.parseInt(strings[0].split("= ")[1]) == quiz.getSortedQuestions().get(i).getAnswer())
                score++;
            i++;
        }

        quiz.addAnswer();
        quizRepository.save(quiz);

        return score;
    }

    public boolean checkCode(String code) {
        return quizRepository.existsById(Long.valueOf(code));
    }
}
