package com.example.ergasiapssd.quiz;

import com.example.ergasiapssd.answer.Answer;
import com.example.ergasiapssd.answer.AnswerRepository;
import com.example.ergasiapssd.answer.QuestionResponse;
import com.example.ergasiapssd.answer.QuestionResponseRepository;
import com.example.ergasiapssd.user.User;
import com.example.ergasiapssd.user.UserRepository;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final MultipleChoiceRepository multipleChoiceRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final QuestionResponseRepository questionResponseRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository,
                       QuestionRepository questionRepository,
                       MultipleChoiceRepository multipleChoiceRepository,
                       UserRepository userRepository,
                       AnswerRepository answerRepository,
                       QuestionResponseRepository questionResponseRepository){
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.multipleChoiceRepository = multipleChoiceRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.questionResponseRepository = questionResponseRepository;
    }

    public Optional<Quiz> getById(UUID id) {
        return quizRepository.findById(id);
    }

    public Page<Quiz> showMyQuizzes(int offset, int pageSize) {
        Pageable sorted = PageRequest.of(offset, pageSize, Sort.by("title"));
        return quizRepository.findQuizzesByUsername(SecurityContextHolder.getContext().getAuthentication().getName(), sorted);
    }

    public Page<Answer> answersOfQuizWithId(UUID quizId, int offset, int pageSize) {
        Pageable sorted = PageRequest.of(offset, pageSize, Sort.by("id"));
        return answerRepository.findAnswersByQuizId(quizId, sorted);
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

    public boolean deleteQuiz(UUID id) {
        Optional<Quiz> quizOptional = quizRepository.findById(id);

        if(quizOptional.isEmpty())
            throw new IllegalStateException("Quiz with id " + id + "does not exist.");

        if (!quizOptional.get().getCreator().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName()) &&
                quizOptional.get().getCreator().getRoles().contains("ROLE_ADMIN"))
            return false;

        quizRepository.deleteById(id);
        return true;
    }

    public Pair<Integer, Integer> submitSolve(Map<String,String> allRequestParams, UUID id) {
        Optional<Quiz> quizOptional = quizRepository.findById(id);
        Optional<User> userOptional = userRepository.findUserByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );

        if (quizOptional.isEmpty() || userOptional.isEmpty())
            return new Pair<>(-1, -1);

        Quiz quiz = quizOptional.get();

        Answer answer = new Answer();

        int score = 0;
        int i = 0;

        for (var entry : allRequestParams.entrySet()) {
            QuestionResponse questionResponse = new QuestionResponse();
            int an = Integer.parseInt(entry.getValue().split(",")[0].split("= ")[1]);

            if (an == quiz.getSortedQuestions().get(i).getAnswer())
                score++;

            questionResponse.setResponse(an);
            Optional<Question> questionOptional = questionRepository.findById(Long.valueOf(entry.getKey()));
            questionOptional.ifPresent(questionResponse::setQuestion);
            questionResponseRepository.save(questionResponse);
            answer.addQuestionResponse(questionResponse);

            i++;
        }

        answer.setUser(userOptional.get());
        answer.setScore(score);
        answerRepository.save(answer);
        quiz.getAnswers().add(answer);
        quizRepository.save(quiz);

        return new Pair<>(score, allRequestParams.size());
    }

    public boolean checkCode(String code) {
        return quizRepository.existsById(UUID.fromString(code));
    }

    public void lockUnlock(UUID quizId) {
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);

        if (quizOptional.isEmpty())
            return;

        Quiz quiz = quizOptional.get();
        quiz.setClosed(!quiz.isClosed());
        quizRepository.save(quiz);
    }
}
