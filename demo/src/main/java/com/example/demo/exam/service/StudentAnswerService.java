package com.example.demo.exam.service;

import com.example.demo.exam.dto.RecordedAnswerDTO;
import com.example.demo.exam.dto.WrittenAnswerDTO;
import com.example.demo.exam.model.StudentAnswer;
import com.example.demo.exam.repository.StudentAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentAnswerService {
    private final StudentAnswerRepository studentAnswerRepository;

//    public WrittenAnswerDTO getWrittenAnswers(Long quizId) {
//        List<StudentAnswer> answers = studentAnswerRepository.findNotEvaluatedAnswersByQuizId(quizId);
//    }

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
