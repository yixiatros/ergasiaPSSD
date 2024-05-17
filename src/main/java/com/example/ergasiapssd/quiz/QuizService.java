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
            }
        }

        if (newQuiz.getQuestions().size() == 0)
            return;

        quizRepository.save(newQuiz);
    }

    public void deleteQuiz(Long id) {
        boolean exists = quizRepository.existsById(id);

        if(!exists)
            throw new IllegalStateException("Quiz with id " + id + "does not exist.");

        quizRepository.deleteById(id);
    }
}
