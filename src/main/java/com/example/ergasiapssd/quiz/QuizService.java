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

    public List<Answer> getAnswersOfUserToQuiz(String username, UUID quizId) {
        Optional<User> userOptional = userRepository.findUserByUsername(username);

        if (userOptional.isEmpty())
            return List.of();

        return answerRepository.getAnswersOfUserToQuizByIds(userOptional.get().getId(), quizId);
    }

    public List<User> getUsersByRole(String role, String search){
        if (search == null || search.equals(""))
            return userRepository.findAllUsersByRole(role);

        return userRepository.findAllUsersByRoleAndSearch(role, search);
    }
    public List<User> getUsersByRole(String role) {
        return getUsersByRole(role, "");
    }

    public Page<Quiz> showMyQuizzes(int offset, int pageSize) {
        Pageable sorted = PageRequest.of(offset, pageSize, Sort.by("title"));
        return quizRepository.findQuizzesByUsername(SecurityContextHolder.getContext().getAuthentication().getName(), sorted);
    }

    public Page<Answer> answersOfQuizWithId(UUID quizId, int offset, int pageSize) {
        Pageable sorted = PageRequest.of(offset, pageSize, Sort.by("id"));
        return answerRepository.findAnswersByQuizId(quizId, sorted);
    }

    public boolean createQuiz(Map<String,String> allRequestParams) {

        Quiz newQuiz = new Quiz();

        Optional<User> userOptional = userRepository.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (userOptional.isEmpty())
            return false;

        newQuiz.setCreator(userOptional.get());

        if (setQuizFromParams(allRequestParams, newQuiz))
            return false;

        return true;
    }

    public Pair<String, String> deleteQuiz(UUID id) {
        Optional<Quiz> quizOptional = quizRepository.findById(id);

        if(quizOptional.isEmpty())
            return new Pair<>("danger", "Quiz with id " + id + "does not exist.");

        if (!quizOptional.get().getCreator().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName()) &&
                quizOptional.get().getCreator().getRoles().contains("ROLE_ADMIN"))
            return new Pair<>("danger", "You are not authorized to do this action.");

        quizRepository.deleteById(id);
        return new Pair<>("warning", "The quiz has been deleted.");
    }

    public boolean editView(UUID id) {
        Optional<Quiz> quizOptional = getById(id);
        Optional<User> userOptional = userRepository.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if (quizOptional.isEmpty() || userOptional.isEmpty())
            return false;

        if (quizOptional.get().getCreator().getId() == userOptional.get().getId())
            return false;

        return true;
    }

    public void edit(UUID id, Map<String,String> allRequestParams) {
        Optional<Quiz> quizOptional = quizRepository.findById(id);
        Optional<User> userOptional = userRepository.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if (userOptional.isEmpty() || quizOptional.isEmpty())
            return;

        Quiz quiz = quizOptional.get();
        quiz.setQuestions(new HashSet<>());

        setQuizFromParams(allRequestParams, quiz);
    }

    private boolean setQuizFromParams(Map<String, String> allRequestParams, Quiz quiz) {
        Question newQuestion = null;
        boolean isQuestionAddedToQuiz = false;

        for (var entry : allRequestParams.entrySet()) {
            if (entry.getKey().equals("title")){
                quiz.setTitle(entry.getValue());
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
                        quiz.addQuestion(newQuestion);
                        isQuestionAddedToQuiz = true;
                    }
                }
            } else if (entry.getKey().startsWith("radio")) {
                if (newQuestion != null)
                    newQuestion.setAnswer(Integer.parseInt(entry.getValue()));
            }
        }

        if (quiz.getQuestions().size() == 0)
            return false;

        quizRepository.save(quiz);
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

    public Pair<String, String> lockUnlock(UUID quizId) {
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);

        if (quizOptional.isEmpty())
            return new Pair<>("danger", "Something went wrong. The quiz was not found.");

        Quiz quiz = quizOptional.get();
        quiz.setClosed(!quiz.isClosed());
        quizRepository.save(quiz);

        if (quiz.isClosed())
            return new Pair<>("success", "The quiz has been CLOSED successfully.");
        else
            return new Pair<>("success", "The quiz has been OPENED successfully.");
    }

    public boolean shareQuiz(UUID quizId, Long userId) {
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (quizOptional.isEmpty() || userOptional.isEmpty())
            return false;

        Quiz quiz = quizOptional.get();
        quiz.getVisibleUsers().add(userOptional.get());
        quizRepository.save(quiz);

        return true;
    }

    public List<Quiz> getAvailableQuizzes() {
        Optional<User> userOptional = userRepository.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if (userOptional.isEmpty())
            return List.of();

        return quizRepository.findAllAvailableQuizzesOfUserById(userOptional.get().getId());
    }
}
