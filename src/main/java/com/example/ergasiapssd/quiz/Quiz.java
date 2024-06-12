package com.example.ergasiapssd.quiz;

import com.example.ergasiapssd.answer.Answer;
import com.example.ergasiapssd.user.User;
import com.example.ergasiapssd.user.role.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private boolean isClosed;

    @ManyToOne
    private User creator;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "quizzes_questions",
            joinColumns = @JoinColumn(
                    name = "quiz_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "question_id", referencedColumnName = "id"))
    private Set<Question> questions = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "quizzes_answers",
            joinColumns = @JoinColumn(
                    name = "quiz_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "answer_id", referencedColumnName = "id"))
    private Set<Answer> answers = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "quizzes_availability",
            joinColumns = @JoinColumn(
                    name = "quiz_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"))
    private Set<User> visibleUsers = new HashSet<>();


    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public List<Question> getSortedQuestions() {
        List<Question> items = new ArrayList<>(getQuestions().stream().toList());

        items.sort(Comparator.comparingLong(Question::getId));

        return items;
    }
}
