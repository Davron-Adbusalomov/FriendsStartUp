package com.example.demo.test.dto;

import com.example.demo.test.model.Answer;

import java.util.List;

public class QuestionDTO {
    private Long question_id;

    private String question;

    private List<AnswerDTO> answers;

    public QuestionDTO(Long question_id, String question, List<AnswerDTO> answers) {
        this.question_id = question_id;
        this.question = question;
        this.answers = answers;
    }

    public Long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(Long question_id) {
        this.question_id = question_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }
}
