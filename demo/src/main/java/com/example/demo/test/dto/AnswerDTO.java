package com.example.demo.test.dto;

import com.example.demo.test.model.Question;

public class AnswerDTO {
    private Long id;

    private String answer;

    private boolean isCorrect;

    private QuestionDTO question;

    public AnswerDTO(){

    }

    public AnswerDTO(Long id, String answer, boolean isCorrect, QuestionDTO question) {
        this.id = id;
        this.answer = answer;
        this.isCorrect = isCorrect;
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public QuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDTO question) {
        this.question = question;
    }
}
