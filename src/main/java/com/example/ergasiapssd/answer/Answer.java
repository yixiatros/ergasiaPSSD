package com.example.ergasiapssd.answer;

import com.example.ergasiapssd.quiz.Question;
import com.example.ergasiapssd.quiz.Quiz;
import com.example.ergasiapssd.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "answer", cascade = CascadeType.ALL)
    private Set<QuestionResponse> responses = new HashSet<>();

    private float score;


    public void addQuestionResponse(QuestionResponse questionResponse) {
        this.responses.add(questionResponse);
    }

}
