package com.example.demo.exam.service;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.config.TenantContext;
import com.example.demo.enums.QuizContentStatus;
import com.example.demo.exam.dto.*;
import com.example.demo.exam.mapper.QuestionMapper;
import com.example.demo.exam.mapper.QuizMapper;
import com.example.demo.exam.model.*;
import com.example.demo.exam.repository.*;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.management.specification.QuizSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;

    private final TeacherRepository teacherRepository;

    private final GroupRepository groupRepository;

    private final QuestionRepository questionRepository;

    private final StudentRepository studentRepository;

    private final Quiz_ResultsRepository quizResultsRepository;

    private final WrittenQuestionsRepository writtenQuestionsRepository;

    private final WrongAnswersAnalyzeRepository wrongAnswersAnalyzeRepository;


    @Transactional
    public ResponseEntity<?> createQuiz(QuizDTOForRequest quizDTO) {

        if (!teacherRepository.existsById(quizDTO.getTeacherId())) {
            return ResponseEntity.badRequest().body("No teacher with id " + quizDTO.getTeacherId());
        }

        if (!groupRepository.existsById(quizDTO.getGroupingId())) {
            return ResponseEntity.badRequest().body("No group with id " + quizDTO.getGroupingId());
        }


        LocalDateTime startTime = Instant.ofEpochMilli(quizDTO.getStartTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        List<Question> questions = questionRepository.findAllById(quizDTO.getQuestions());

        if (questions.size() != quizDTO.getQuestions().size()) {
            return ResponseEntity.badRequest().body("One or more questions not found");
        }

        Quiz quiz = new Quiz();
        quiz.setTeacherId(quizDTO.getTeacherId());
        quiz.setDuration(quizDTO.getDuration());
        quiz.setStartTime(startTime);
        quiz.setGroupingId(quizDTO.getGroupingId());
        quiz.setQuestionsNum(quizDTO.getQuestions_num());
        quiz.setCenterId(TenantContext.getCenterId());

        quiz.setQuestions(new HashSet<>(questions));

        quizRepository.save(quiz);

        return ResponseEntity.ok("Quiz created successfully!");
    }

    @Transactional
    public QuizDTO beginQuiz(UUID quizId) {

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new EntityNotFoundException("No quiz found with this id"));

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime quizStartTime = quiz.getStartTime();
        Long quizDuration = quiz.getDuration();
        LocalDateTime quizEndTime = quizStartTime.plusMinutes(quizDuration);

        if (currentTime.isBefore(quizStartTime) || currentTime.isAfter(quizEndTime)) {
            throw new IllegalStateException("Quiz is not currently active or has ended");
        }

        Set<QuestionDTO> allQuestions = quiz.getQuestions().stream().map(QuestionMapper::toDTO).collect(java.util.stream.Collectors.toSet());
        List<QuestionDTO> easyQuestions = new ArrayList<>();
        List<QuestionDTO> mediumQuestions = new ArrayList<>();
        List<QuestionDTO> hardQuestions = new ArrayList<>();

        int numOfEasyQuestions = (int) (quiz.getQuestionsNum() * 0.4);
        int numOfMediumQuestions = (int) (quiz.getQuestionsNum() * 0.3);
        int numOfHardQuestions = quiz.getQuestionsNum() - numOfMediumQuestions - numOfEasyQuestions;

        for (QuestionDTO question : allQuestions) {
            if (question.getMark() == 1) {
                easyQuestions.add(question);
            } else if (question.getMark() == 2) {
                mediumQuestions.add(question);
            } else {
                hardQuestions.add(question);
            }
        }

        if (quiz.getQuestionsNum() > 0 && quiz.getQuestionsNum() <= allQuestions.size()) {

            Collections.shuffle(easyQuestions);
            Collections.shuffle(mediumQuestions);
            Collections.shuffle(hardQuestions);

            Set<QuestionDTO> selectedQuestions = new HashSet<>();

            for (int i = 0; i < numOfEasyQuestions; i++) {
                selectedQuestions.add(easyQuestions.get(i));
            }

            for (int i = 0; i < numOfMediumQuestions; i++) {
                selectedQuestions.add(mediumQuestions.get(i));
            }

            for (int i = 0; i < numOfHardQuestions; i++) {
                selectedQuestions.add(hardQuestions.get(i));
            }

            QuizDTO shuffledQuiz = new QuizDTO();
            shuffledQuiz.setId(quiz.getId());
            shuffledQuiz.setQuestionsNum(quiz.getQuestionsNum());
            shuffledQuiz.setDuration(quiz.getDuration());
            shuffledQuiz.setGroupingId(quiz.getGrouping().getId());
            shuffledQuiz.setTeacherId(quiz.getTeacher().getId());
            shuffledQuiz.setQuestions(selectedQuestions);

            return shuffledQuiz;
        }

        return QuizMapper.toDTO(quiz);
    }

    @Transactional
    public String checkingMultipleChoiceQuestions(List<Response> responseList, Long studentId, UUID quizId) {
        LocalDateTime currentTime = LocalDateTime.now();

        if (!studentRepository.existsById(studentId)) {
            throw new EntityNotFoundException("No student found with this id");
        }
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("No quiz found with this id"));

        LocalDateTime quizStartTime = quiz.getStartTime();
        Long quizDuration = quiz.getDuration();
        LocalDateTime quizEndTime = quizStartTime.plusMinutes(quizDuration + 1);

        if (currentTime.isBefore(quizStartTime) || currentTime.isAfter(quizEndTime)) {
            throw new IllegalStateException("Quiz is not currently active or has ended");
        }

        WrongAnswersAnalyze wrongAnswersAnalyze = new WrongAnswersAnalyze();
        Quiz_Results quizResults = new Quiz_Results();
        long mark = 0L;

        for (Response response : responseList) {

            Question question = questionRepository.findById(response.getQuestion_id()).orElseThrow(() -> new EntityNotFoundException("No question found with this id"));

            if (!question.getType().equals("MCQ")) {
                WrittenQuestions writtenQuestions = new WrittenQuestions();
                writtenQuestions.setQuestionId(question.getId());
                writtenQuestions.setQuizId(quizId);
                writtenQuestions.setStudentAnswer(response.getAnswer());
                writtenQuestions.setStudentId(studentId);
                writtenQuestions.setCorrect_answer(question.getRight_answer());
                writtenQuestions.setQuestionTitle(question.getTitle());
                writtenQuestions.setMax_score((long) question.getMark());
                writtenQuestionsRepository.save(writtenQuestions);
            } else if (question.getRight_answer().equals(response.getAnswer())) {
                mark += question.getMark();
            } else {
                wrongAnswersAnalyze.setQuestion_id(response.getQuestion_id());
                wrongAnswersAnalyze.setWrong_answer(response.getAnswer());
            }

        }
        wrongAnswersAnalyze.setStudentId(studentId);
        wrongAnswersAnalyze.setQuiz_id(quizId);
        wrongAnswersAnalyzeRepository.save(wrongAnswersAnalyze);

        quizResults.setQuiz(quizRepository.findById(quizId).get());
        quizResults.setMark(mark);
        quizResults.setStudentId(studentId);
        quizResults.setCenterId(TenantContext.getCenterId());
        quizResultsRepository.save(quizResults);

        return "Successfully recorded!";
    }

    @Transactional
    public UpcomingTaskInfo getUpcomingQuizInfo() {
        Long userId = CurrentUserUtils.getUserId();
        Student student = studentRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + userId));
        if (student.getGroupings().isEmpty()) {
            throw new EntityNotFoundException("Student is not assigned to any group");
        }
        Quiz quiz = quizRepository.findUpcomingQuizByGroupIds(
                student.getGroupings().stream().map(Grouping::getId).toList(),
                LocalDateTime.now()
        );
        if (quiz == null) {
            return null;
        }

        UpcomingTaskInfo upcomingTaskInfo = new UpcomingTaskInfo();
        upcomingTaskInfo.setTitle(quiz.getTitle());
        upcomingTaskInfo.setTime(quiz.getStartTime());

        return upcomingTaskInfo;
    }

    public Page<QuizSummaryDTO> getQuizzesList(
            UUID groupId,
            String title,
            QuizContentStatus status,
            Pageable pageable
    ) {
        Specification<Quiz> spec = QuizSpecification.advancedFilter(groupId, title, status);

        Pageable effectivePageable = pageable != null
                ? PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().and(Sort.by("startTime").ascending())
        )
                : PageRequest.of(0, 10, Sort.by("startTime").ascending());

        LocalDateTime now = LocalDateTime.now();

        return quizRepository.findAll(spec, effectivePageable)
                .map(quiz -> toSummaryDTOWithStatus(quiz, now));
    }


    private QuizSummaryDTO toSummaryDTOWithStatus(Quiz quiz, LocalDateTime now) {
        QuizSummaryDTO dto = QuizMapper.toSummaryDTO(quiz);

        LocalDateTime start = quiz.getStartTime();
        LocalDateTime end = quiz.getEndTime();

        if (start == null || end == null) {
            dto.setStatus(QuizContentStatus.PENDING);
            return dto;
        }

        if (start.isAfter(now)) {
            dto.setStatus(QuizContentStatus.PENDING);
        } else if (end.isBefore(now)) {
            dto.setStatus(QuizContentStatus.COMPLETED);
        } else {
            dto.setStatus(QuizContentStatus.ONGOING);
        }

        return dto;
    }


}
