package com.example.ergasiapssd.quiz;

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
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String question;

    private int answer;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "questions_multiplechoices",
            joinColumns = @JoinColumn(
                    name = "question_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "multiple_choice_id", referencedColumnName = "id"))
    private Set<MultipleChoice> multipleChoices = new HashSet<>();


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public Set<MultipleChoice> getMultipleChoices() {
        return multipleChoices;
    }

    public void setMultipleChoices(Set<MultipleChoice> multipleChoices) {
        this.multipleChoices = multipleChoices;
    }

    public void addMultipleChoice(MultipleChoice multipleChoice) {
        this.multipleChoices.add(multipleChoice);
    }

    public List<MultipleChoice> getSortedMultipleChoices() {
        List<MultipleChoice> items = new ArrayList<>(getMultipleChoices().stream().toList());

        items.sort(Comparator.comparingLong(MultipleChoice::getId));

        return items;
    }
}
