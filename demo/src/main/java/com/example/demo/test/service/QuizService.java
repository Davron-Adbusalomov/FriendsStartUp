package com.example.demo.test.service;

import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.test.dto.QuizDTO;
import com.example.demo.test.dto.QuizDTOForRequest;
import com.example.demo.test.mapper.QuizMapper;
import com.example.demo.test.model.*;
import com.example.demo.test.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private Quiz_ResultsRepository quizResultsRepository;

    @Autowired
    private WrittenQuestionsRepository writtenQuestionsRepository;

    @Autowired
    private WrongAnswersAnalyzeRepository wrongAnswersAnalyzeRepository;



    public ResponseEntity<?> createQuiz(QuizDTOForRequest quizDTO) {
        Optional<Teacher> teacher = teacherRepository.findById(quizDTO.getTeacherId());
        if (teacher.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No teacher with this id: " + quizDTO.getTeacherId());
        }

        Optional<Grouping> group = groupRepository.findById(quizDTO.getGroupingId());
        if (group.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No group with this id: " + quizDTO.getGroupingId());
        }

        Quiz quiz = new Quiz();
        quiz.setTeacher(teacher.get());
        quiz.setDuration(quizDTO.getDuration());
        quiz.setGrouping(group.get());
        quiz.setQuestions_num(quizDTO.getQuestions_num());
        for (Long questionId : quizDTO.getQuestions()) {
            Optional<Question> question = questionRepository.findById(questionId);
            if (question.isEmpty()){
                throw new EntityNotFoundException("No question found with this id: "+questionId);
            }
            quiz.assignQuestion(question.get());
        }
        quizRepository.save(quiz);
        return ResponseEntity.status(HttpStatus.OK).body("Quiz created successfully!");
    }



    public QuizDTO beginQuiz(Long quizId) {
        Optional<Quiz> optionalQuiz = quizRepository.findById(quizId);

        if (optionalQuiz.isEmpty()) {
            throw new EntityNotFoundException("No quiz found with this id");
        }

        Quiz quiz = optionalQuiz.get();
        List<Question> allQuestions = quiz.getQuestions();
        List<Question> easyQuestions = new ArrayList<>();
        List<Question> mediumQuestions = new ArrayList<>();
        List<Question> hardQuestions = new ArrayList<>();

        int numOfEasyQuestions = (int) (optionalQuiz.get().getQuestions_num()*0.4);
        int numOfMediumQuestions = (int) (optionalQuiz.get().getQuestions_num()*0.3);
        int numOfHardQuestions = quiz.getQuestions_num()-numOfMediumQuestions-numOfEasyQuestions;

        for(Question question:allQuestions){
            if (question.getMark()==1){
                easyQuestions.add(question);
            }
            else if (question.getMark()==2){
                mediumQuestions.add(question);
            }
            else{
                hardQuestions.add(question);
            }
        }

        if (quiz.getQuestions_num() > 0 && quiz.getQuestions_num() <= allQuestions.size()) {

            Collections.shuffle(easyQuestions);
            Collections.shuffle(mediumQuestions);
            Collections.shuffle(hardQuestions);

            List<Question> selectedQuestions = new ArrayList<>();

            for (int i=0; i<numOfEasyQuestions; i++){
                selectedQuestions.add(easyQuestions.get(i));
            }

            for (int i=0; i<numOfMediumQuestions; i++){
                selectedQuestions.add(mediumQuestions.get(i));
            }

            for (int i=0; i<numOfHardQuestions; i++){
                selectedQuestions.add(hardQuestions.get(i));
            }

            QuizDTO shuffledQuiz = new QuizDTO();
            shuffledQuiz.setId(quiz.getId());
            shuffledQuiz.setQuestions_num(quiz.getQuestions_num());
            shuffledQuiz.setDuration(quiz.getDuration());
            shuffledQuiz.setGroupingId(quiz.getGrouping().getId());
            shuffledQuiz.setTeacherId(quiz.getTeacher().getId());
            shuffledQuiz.setQuestions(selectedQuestions);

            return shuffledQuiz;
        }

        return QuizMapper.toDTO(quiz);
    }

    public String checkingMultipleChoiceQuestions(List<Response> responseList, Long id, Long quizId){
        LocalDateTime currentTime = LocalDateTime.now();

        Optional<Quiz> optionalQuiz = quizRepository.findById(quizId);
        if (optionalQuiz.isEmpty()) {
            throw new EntityNotFoundException("No quiz found with this id");
        }
        Quiz quiz = optionalQuiz.get();

        LocalDateTime quizStartTime = quiz.getStartTime();
        Long quizDuration = quiz.getDuration();
        LocalDateTime quizEndTime = quizStartTime.plusMinutes(quizDuration+1);

        if (currentTime.isBefore(quizStartTime) || currentTime.isAfter(quizEndTime)) {
            throw new IllegalStateException("Quiz is not currently active or has ended");
        }

        WrongAnswersAnalyze wrongAnswersAnalyze = new WrongAnswersAnalyze();
        Quiz_Results quizResults = new Quiz_Results();
        long mark = 0L;

        for (Response response : responseList) {

            Optional<Question> optionalQuestion = questionRepository.findById(response.getQuestion_id());
            if (optionalQuestion.isEmpty()){
                throw new EntityNotFoundException("There is no this question in this quiz!");
            }
            Question question = optionalQuestion.get();

            if (!question.getType().equals("Multiple Choice")){
                WrittenQuestions writtenQuestions = new WrittenQuestions();
                writtenQuestions.setQuestionId(question.getId());
                writtenQuestions.setQuizId(quizId);
                writtenQuestions.setStudentAnswer(response.getAnswer());
                writtenQuestions.setStudent(studentRepository.findById(id).get());
                writtenQuestions.setCorrect_answer(question.getRight_answer());
                writtenQuestions.setQuestionTitle(question.getTitle());
                writtenQuestions.setMax_score((long) question.getMark());
                writtenQuestionsRepository.save(writtenQuestions);
            }
            else if (question.getRight_answer().equals(response.getAnswer())){
                mark+=question.getMark();
            }
            else {
                wrongAnswersAnalyze.setQuestion_id(response.getQuestion_id());
                wrongAnswersAnalyze.setWrong_answer(response.getAnswer());
            };

        }
        wrongAnswersAnalyze.setStudent(studentRepository.findById(id).get());
        wrongAnswersAnalyze.setQuiz_id(quizId);
        wrongAnswersAnalyzeRepository.save(wrongAnswersAnalyze);

        quizResults.setQuiz(quizRepository.findById(quizId).get());
        quizResults.setMark(mark);
        quizResults.setStudent(studentRepository.findById(id).get());
        quizResultsRepository.save(quizResults);

        return "Successfully recorded!";
    }

}
