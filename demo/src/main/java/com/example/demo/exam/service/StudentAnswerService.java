package com.example.demo.exam.service;

import com.example.demo.exam.dto.WrittenAnswerDTO;
import com.example.demo.exam.model.StudentAnswer;
import com.example.demo.exam.repository.StudentAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentAnswerService {
    private final StudentAnswerRepository studentAnswerRepository;

//    public WrittenAnswerDTO getWrittenAnswers(Long quizId) {
//        List<StudentAnswer> answers = studentAnswerRepository.findNotEvaluatedAnswersByQuizId(quizId);
//
//
//    }
}
