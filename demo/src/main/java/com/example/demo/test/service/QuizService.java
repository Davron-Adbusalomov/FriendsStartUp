package com.example.demo.test.service;

import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.test.dto.CheckingQuizDTO;
import com.example.demo.test.dto.QuizDTO;
import com.example.demo.test.model.Question;
import com.example.demo.test.model.Quiz;
import com.example.demo.test.model.Quiz_Results;
import com.example.demo.test.model.Response;
import com.example.demo.test.repository.QuestionRepository;
import com.example.demo.test.repository.QuizRepository;
import com.example.demo.test.repository.Quiz_ResultsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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



    public ResponseEntity<?> createQuiz(QuizDTO quizDTO) {
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
        for (Question question : quizDTO.getQuestions()) {
            quiz.assignQuestion(question);
        }
        quizRepository.save(quiz);
        return ResponseEntity.status(HttpStatus.OK).body("Quiz created successfully!");
    }



    public Quiz beginQuiz(Long quizId) {
        Optional<Quiz> optionalQuiz = quizRepository.findById(quizId);

        if (optionalQuiz.isEmpty()) {
            throw new EntityNotFoundException("No quiz found with this id");
        }

        Quiz quiz = optionalQuiz.get();
        Set<Question> allQuestions = quiz.getQuestions();

        if (quiz.getQuestions_num() > 0 && quiz.getQuestions_num() < allQuestions.size()) {
            List<Question> questionsList = new ArrayList<>(allQuestions);
            Collections.shuffle(questionsList);

            Set<Question> selectedQuestions = new HashSet<>(questionsList.subList(0, quiz.getQuestions_num()));

            Quiz shuffledQuiz = new Quiz();
            shuffledQuiz.setId(quiz.getId());
            shuffledQuiz.setQuestions_num(quiz.getQuestions_num());
            shuffledQuiz.setDuration(quiz.getDuration());
            shuffledQuiz.setGrouping(quiz.getGrouping());
            shuffledQuiz.setTeacher(quiz.getTeacher());
            shuffledQuiz.setQuestions(selectedQuestions);

            return shuffledQuiz;
        }
        return quiz;
    }

    public CheckingQuizDTO checkingMultipleChoiceQuestions(List<Response> responseList, Long id){
        CheckingQuizDTO dto = new CheckingQuizDTO();
        long mark = 0L;
        List<Response> wrong_answers = new ArrayList<>();
        List<Response> written_questions = new ArrayList<>();

        for (Response response : responseList) {
            Optional<Question> optionalQuestion = questionRepository.findById(response.getQuestion_id());
            if (optionalQuestion.isEmpty()){
                throw new EntityNotFoundException("There is no this question in this quiz!");
            }
            Question question = optionalQuestion.get();

            if (!question.getType().equals("Multiple Choice")){
                written_questions.add(response);
            }
            else if (question.getRight_answer().equals(response.getAnswer())){
                mark+=question.getMark();
            }
            else wrong_answers.add(response);
        }

        dto.setWrong_answers(wrong_answers);
        dto.setWritten_questions(written_questions);
        dto.setCurrent_mark(mark);
        dto.setStudent_id(id);

        return dto;
    }
}
