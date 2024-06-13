package com.example.ergasiapssd.answer;

import com.example.ergasiapssd.quiz.Question;
import com.example.ergasiapssd.quiz.Quiz;
import com.example.ergasiapssd.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "answers_responses",
            joinColumns = @JoinColumn(
                    name = "answer_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "response_id", referencedColumnName = "id"))
    private Set<QuestionResponse> responses = new HashSet<>();

    private float score;


    public void addQuestionResponse(QuestionResponse questionResponse) {
        this.responses.add(questionResponse);
    }

    public List<QuestionResponse> getSortedQuestionResponse() {
        List<QuestionResponse> items = new ArrayList<>(getResponses().stream().toList());

        items.sort(Comparator.comparingLong(QuestionResponse::getId));

        return items;
    }
}
