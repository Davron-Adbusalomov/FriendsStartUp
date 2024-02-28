package com.example.demo.test.service;

import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.test.dto.QuestionDTO;
import com.example.demo.test.model.Option;
import com.example.demo.test.model.Question;
import com.example.demo.test.model.Quiz;
import com.example.demo.test.repository.OptionRepository;
import com.example.demo.test.repository.QuestionRepository;
import com.example.demo.test.repository.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuizRepository quizRepository;

    public Question getQuestionById(Long id){
        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()){
            throw new EntityNotFoundException("No question found with this id: " + id);
        }
        return question.get();
    }

    public ResponseEntity<?> getAllQuestions(){
        return ResponseEntity.status(HttpStatus.OK).body(questionRepository.findAll());
    }

    public String createQuestion(QuestionDTO questionDTO) {
        List<Option> arrayList = new ArrayList<>();

        for(int i=0; i<questionDTO.getOptions().size(); i++) {
            Option option = new Option();
            option.setText(questionDTO.getOptions().get(i));
            arrayList.add(option);
        }

            Optional<Teacher> optionalTeacher = teacherRepository.findById(questionDTO.getTeacherId());

            if (optionalTeacher.isPresent()) {
                Teacher teacher = optionalTeacher.get();

                    Question question = new Question();
                    question.setLevel(questionDTO.getLevel());
                    question.setTitle(questionDTO.getTitle());
                    question.setType(questionDTO.getType());
                    question.setMark(questionDTO.getMark());
                    question.setRight_answer(questionDTO.getRight_answer());
                    question.setTeacher(teacher);
                for (Option option:arrayList) {
                    question.assignOption(option);
                }

                questionRepository.save(question);

                for (Option option:arrayList) {
                    option.setQuestion(question);
                    optionRepository.save(option);
                }

                return "Question created successfully.";
            } else {
                throw new EntityNotFoundException("No teacher found with this id!");
            }
    }

    public ResponseEntity<?> deleteQuestion(Long questionId){
        Question question = questionRepository.findById(questionId)
                .orElseThrow(()-> new EntityNotFoundException("Question not found with this id:" + questionId));

        List<Quiz> quizzes = quizRepository.findByQuestionId(questionId);

        for (Quiz quiz: quizzes) {
            quiz.getQuestions().remove(question);
        }

        questionRepository.delete(question);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted");
    }


    public ResponseEntity<?> updateQuestion(QuestionDTO questionDTO,Long id){
        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no question with this id: "+id);
        }

        Question updatedQuestion = question.get();
        updatedQuestion.setMark(questionDTO.getMark());
        updatedQuestion.setType(questionDTO.getType());
        updatedQuestion.setRight_answer(questionDTO.getRight_answer());
        updatedQuestion.setTitle(questionDTO.getTitle());

        questionRepository.save(updatedQuestion);
        return ResponseEntity.status(HttpStatus.OK).body(updatedQuestion);
    }

    public List<Question> getQuestionByLevel(String level) {
        return questionRepository.findQuestionByLevel(level);
    }

}
