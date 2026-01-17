package com.example.demo.exam.service;

import com.example.demo.exam.dto.RecordedAnswerDTO;
import com.example.demo.exam.dto.StudentWrittenAnswersDTO;
import com.example.demo.exam.dto.WrittenAnswerDTO;
import com.example.demo.exam.model.Question;
import com.example.demo.exam.model.StudentAnswer;
import com.example.demo.exam.repository.StudentAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentAnswerService {
    private final StudentAnswerRepository studentAnswerRepository;

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
}
