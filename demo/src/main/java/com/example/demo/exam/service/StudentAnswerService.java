package com.example.demo.exam.service;

import com.example.demo.exam.dto.*;
import com.example.demo.exam.mapper.QuizMapper;
import com.example.demo.exam.model.Question;
import com.example.demo.exam.model.Quiz;
import com.example.demo.exam.model.StudentAnswer;
import com.example.demo.exam.repository.QuizRepository;
import com.example.demo.exam.repository.StudentAnswerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentAnswerService {
    private final StudentAnswerRepository studentAnswerRepository;
    private final QuizRepository quizRepository;

    public List<StudentWrittenAnswersDTO> getWrittenAnswers(UUID quizId) {

        List<Object[]> rows =
                studentAnswerRepository.findNotEvaluatedWrittenAnswersByQuizId(quizId);

        Map<Long, StudentWrittenAnswersDTO> grouped = new LinkedHashMap<>();

        for (Object[] row : rows) {
            StudentAnswer sa = (StudentAnswer) row[0];
            Question q = (Question) row[1];

            StudentWrittenAnswersDTO studentDto =
                    grouped.computeIfAbsent(sa.getStudentId(), studentId -> {
                        StudentWrittenAnswersDTO dto = new StudentWrittenAnswersDTO();
                        dto.setStudentId(studentId);
                        dto.setAnswers(new ArrayList<>());
                        return dto;
                    });

            WrittenAnswerDTO answerDto = new WrittenAnswerDTO();
            answerDto.setQuestionId(q.getId());
            answerDto.setQuestionTitle(q.getTitle());
            answerDto.setStudentAnswer(sa.getAnswer());
            answerDto.setCorrectAnswer(q.getRight_answer());
            answerDto.setScore(sa.getScore());

            studentDto.getAnswers().add(answerDto);
        }

        return new ArrayList<>(grouped.values());
    }



    public List<RecordedAnswerDTO> getRecordedAnswers(UUID quizId, Long studentId) {
        List<StudentAnswer> answers = studentAnswerRepository.findByQuizIdAndStudentId(quizId, studentId);
        return answers.stream().map(answer -> {
            RecordedAnswerDTO dto = new RecordedAnswerDTO();
            dto.setQuizId(answer.getQuizId());
            dto.setQuestionId(answer.getQuestionId());
            dto.setAnswer(answer.getAnswer());
            return dto;
        }).toList();
    }

    public EvaluatedQuizDetailsDTO getEvaluatedQuizDetails(UUID quizId, Long studentId) {
        List<StudentAnswer> answers =
                studentAnswerRepository.findByQuizIdAndStudentId(quizId, studentId);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));

        EvaluatedQuizDetailsDTO quizDetailsDTO =
                QuizMapper.toEvaluatedQuizDetail(quiz);

        Map<UUID, StudentAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(StudentAnswer::getQuestionId, a -> a));

        Set<EvaluatedQuestionDetails> matchedQuestions = quizDetailsDTO.getQuestions().stream()
                .filter(q -> answerMap.containsKey(q.getId()))
                .peek(q -> {
                    StudentAnswer a = answerMap.get(q.getId());
                    q.setStudentAnswer(a.getAnswer());
                    q.setScore(a.getScore());
                    q.setIsCorrect(a.getCorrect());
                })
                .collect(Collectors.toSet());

        quizDetailsDTO.setQuestions(matchedQuestions);

        return quizDetailsDTO;
    }

}
