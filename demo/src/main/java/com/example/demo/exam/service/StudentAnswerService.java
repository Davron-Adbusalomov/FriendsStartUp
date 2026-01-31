package com.example.demo.exam.service;

import com.example.demo.exam.dto.*;
import com.example.demo.exam.mapper.QuestionMapper;
import com.example.demo.exam.mapper.QuizMapper;
import com.example.demo.exam.model.Question;
import com.example.demo.exam.model.Quiz;
import com.example.demo.exam.model.StudentAnswer;
import com.example.demo.exam.repository.QuizRepository;
import com.example.demo.exam.repository.StudentAnswerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        Map<UUID, StudentAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(StudentAnswer::getQuestionId, a -> a));

        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));

        EvaluatedQuizDetailsDTO dto = QuizMapper.toEvaluatedQuizDetail(quiz);

        int maxMark = 0;
        int obtainedMark = 0;

        Set<EvaluatedQuestionDetails> evaluatedQuestions = new HashSet<>();

        for (Question question : quiz.getQuestions()) {

            maxMark += question.getMark();

            StudentAnswer answer = answerMap.get(question.getId());
            if (answer == null) continue;

            EvaluatedQuestionDetails qDto =
                    QuestionMapper.toEvaluatedDetailsDTO(question);

            qDto.setStudentAnswer(answer.getAnswer());
            qDto.setScore(answer.getScore());
            qDto.setIsCorrect(answer.getCorrect());

            obtainedMark += answer.getScore();

            evaluatedQuestions.add(qDto);
        }

        dto.setQuestions(evaluatedQuestions);
        dto.setMaxMark(maxMark);
        dto.setObtainedMark(obtainedMark);

        return dto;
    }


}
