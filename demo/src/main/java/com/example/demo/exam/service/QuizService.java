package com.example.demo.exam.service;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.config.TenantContext;
import com.example.demo.enums.QuizContentStatus;
import com.example.demo.enums.Status;
import com.example.demo.exam.dto.*;
import com.example.demo.exam.mapper.QuestionMapper;
import com.example.demo.exam.mapper.QuizMapper;
import com.example.demo.exam.model.*;
import com.example.demo.exam.repository.*;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.CourseRepository;
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

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;

    private final TeacherRepository teacherRepository;

    private final CourseRepository courseRepository;

    private final GroupRepository groupRepository;

    private final QuestionRepository questionRepository;

    private final StudentRepository studentRepository;

    private final StudentAnswerRepository studentAnswerRepository;

    private final QuizResultsRepository quizResultsRepository;

    private final QuizResultService quizResultService;


    @Transactional
    public ResponseEntity<?> createQuiz(QuizDTOForRequest quizDTO) {

        if (!teacherRepository.existsById(quizDTO.getTeacherId())) {
            return ResponseEntity.badRequest().body("No teacher with id " + quizDTO.getTeacherId());
        }

        if (!courseRepository.existsById(quizDTO.getCourseId())) {
            return ResponseEntity.badRequest().body("No course with id " + quizDTO.getCourseId());
        }

        List<Question> questions = questionRepository.findAllById(quizDTO.getQuestions());

        if (questions.size() != quizDTO.getQuestions().size()) {
            return ResponseEntity.badRequest().body("One or more questions not found");
        }

        Quiz quiz = new Quiz();
        quiz.setTeacherId(quizDTO.getTeacherId());
        quiz.setDuration(quizDTO.getDuration());
        quiz.setStartTime(quizDTO.getStartTime());
        quiz.setEndTime(quizDTO.getStartTime().plusMinutes(quizDTO.getDuration()));
        quiz.setCourseId(quizDTO.getCourseId());
        quiz.setQuestionsNum(quizDTO.getQuestions_num());
        quiz.setTitle(quizDTO.getTitle());
        quiz.setCenterId(TenantContext.getCenterId());

        quiz.setQuestions(new HashSet<>(questions));

        quizRepository.save(quiz);

        return ResponseEntity.ok("Quiz created successfully!");
    }

    @Transactional
    public QuizDTO updateQuiz(UUID quizId, QuizSummaryDTO quizDTO) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new EntityNotFoundException("No quiz found with this id"));

        quiz.setDuration(quizDTO.getDuration());
        quiz.setStartTime(quizDTO.getStartTime());
        quiz.setEndTime(quizDTO.getStartTime().plusMinutes(quizDTO.getDuration()));
        quiz.setTitle(quizDTO.getTitle());
        if (quizDTO.getStatus() == QuizContentStatus.CANCELLED) {
            quiz.setStatus(Status.INACTIVE);
        }

        if (quizDTO.getQuestionIds() != null) {
            List<Question> questions = questionRepository.findAllById(quizDTO.getQuestionIds());
            quiz.setQuestions(new HashSet<>(questions));
            quiz.setQuestionsNum(questions.size());
        }

        Quiz updatedQuiz = quizRepository.save(quiz);

        return QuizMapper.toDTO(updatedQuiz);
    }

//    @Transactional
//    public QuizDTO beginQuiz(UUID quizId) {
//
//        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new EntityNotFoundException("No quiz found with this id"));
//
//        validateQuizActive(quizId);
//
//        Set<QuestionDTO> allQuestions = quiz.getQuestions().stream().map(QuestionMapper::toDTO).collect(java.util.stream.Collectors.toSet());
//        List<QuestionDTO> easyQuestions = new ArrayList<>();
//        List<QuestionDTO> mediumQuestions = new ArrayList<>();
//        List<QuestionDTO> hardQuestions = new ArrayList<>();
//
//        int numOfEasyQuestions = (int) (quiz.getQuestionsNum() * 0.4);
//        int numOfMediumQuestions = (int) (quiz.getQuestionsNum() * 0.3);
//        int numOfHardQuestions = quiz.getQuestionsNum() - numOfMediumQuestions - numOfEasyQuestions;
//
//        for (QuestionDTO question : allQuestions) {
//            if (question.getLevel().equals("1")) {
//                easyQuestions.add(question);
//            } else if (question.getLevel().equals("2")) {
//                mediumQuestions.add(question);
//            } else {
//                hardQuestions.add(question);
//            }
//        }
//
//        if (quiz.getQuestionsNum() > 0 && quiz.getQuestionsNum() <= allQuestions.size()) {
//
//            Collections.shuffle(easyQuestions);
//            Collections.shuffle(mediumQuestions);
//            Collections.shuffle(hardQuestions);
//
//            Set<QuestionDTO> selectedQuestions = new HashSet<>();
//
//            for (int i = 0; i < numOfEasyQuestions; i++) {
//                selectedQuestions.add(easyQuestions.get(i));
//            }
//
//            for (int i = 0; i < numOfMediumQuestions; i++) {
//                selectedQuestions.add(mediumQuestions.get(i));
//            }
//
//            for (int i = 0; i < numOfHardQuestions; i++) {
//                selectedQuestions.add(hardQuestions.get(i));
//            }
//
//            QuizDTO shuffledQuiz = new QuizDTO();
//            shuffledQuiz.setId(quiz.getId());
//            shuffledQuiz.setQuestionsNum(quiz.getQuestionsNum());
//            shuffledQuiz.setDuration(quiz.getDuration());
//            shuffledQuiz.setGroupingId(quiz.getGrouping().getId());
//            shuffledQuiz.setTeacherId(quiz.getTeacher().getId());
//            shuffledQuiz.setQuestions(selectedQuestions);
//            shuffledQuiz.setStartTime(quiz.getStartTime());
//            shuffledQuiz.setTitle(quiz.getTitle());
//
//            return shuffledQuiz;
//        }
//
//        return QuizMapper.toDTO(quiz);
//    }

    @Transactional
    public QuizDTO beginQuiz(UUID quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("No quiz found"));

        validateQuizActive(quizId);

        Long studentId = CurrentUserUtils.getUserId();
        if (quizResultsRepository.existsByStudentIdAndQuizId(studentId, quizId)) {
            throw new IllegalStateException("You have already completed this quiz");
        }

        List<QuestionDTO> easy = new ArrayList<>();
        List<QuestionDTO> medium = new ArrayList<>();
        List<QuestionDTO> hard = new ArrayList<>();

        // 1️⃣ split in ONE pass (O(n))
        for (Question q : quiz.getQuestions()) {
            QuestionDTO dto = QuestionMapper.toDTO(q);
            switch (dto.getLevel()) {
                case "1" -> easy.add(dto);
                case "2" -> medium.add(dto);
                case "3" -> hard.add(dto);
            }
        }

        int total = quiz.getQuestionsNum();

        int easyTarget   = Math.round(total * 0.4f);
        int mediumTarget = Math.round(total * 0.3f);
        int hardTarget   = total - easyTarget - mediumTarget;

        // 2️⃣ shuffle once (O(n))
        Collections.shuffle(easy);
        Collections.shuffle(medium);
        Collections.shuffle(hard);

        List<QuestionDTO> selected = new ArrayList<>(total);

        take(selected, easy, easyTarget);
        take(selected, medium, mediumTarget);
        take(selected, hard, hardTarget);

        // 4️⃣ fill missing slots (O(n))
        if (selected.size() < total) {
            fill(selected, total, easy, medium, hard);
        }

        // 5️⃣ final shuffle for fairness
        Collections.shuffle(selected);

        QuizDTO dto = QuizMapper.toDTO(quiz);
        dto.setQuestions(new HashSet<>(selected));

        // Give student only the time remaining until quiz ends, not the full duration
        long remainingSeconds = java.time.Duration.between(LocalDateTime.now(), quiz.getEndTime()).getSeconds();
        long effectiveDuration = Math.min(quiz.getDuration(), Math.max(0L, (remainingSeconds + 59) / 60));
        dto.setDuration(effectiveDuration);

        return dto;
    }

    private void take(List<QuestionDTO> out, List<QuestionDTO> src, int count) {
        int limit = Math.min(count, src.size());
        for (int i = 0; i < limit; i++) {
            out.add(src.get(i));
        }
    }

    private void fill(List<QuestionDTO> out, int total,
                      List<QuestionDTO>... pools) {

        for (List<QuestionDTO> pool : pools) {
            for (QuestionDTO q : pool) {
                if (out.size() == total) return;
                if (!out.contains(q)) out.add(q);
            }
        }
    }

    @Transactional
    public void submitAnswer(UUID quizId, Long studentId, Response response) {

        validateQuizActive(quizId);

        Question question = questionRepository
                .findById(response.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("No question found with this id"));

        StudentAnswer answer = studentAnswerRepository
                .findByStudentIdAndQuizIdAndQuestionId(
                        studentId, quizId, question.getId()
                )
                .orElse(new StudentAnswer());

        answer.setStudentId(studentId);
        answer.setQuizId(quizId);
        answer.setQuestionId(question.getId());
        answer.setAnswer(response.getAnswer());
        answer.setCenterId(TenantContext.getCenterId());

        studentAnswerRepository.save(answer);
    }

    @Transactional
    public void finishQuiz(Long studentId, UUID quizId, UUID groupId) {
        quizResultService.evaluateQuiz(quizId, studentId, groupId);
    }

    private void validateQuizActive(UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("No quiz found with this id"));

        LocalDateTime currentTime = LocalDateTime.now();

        LocalDateTime quizStartTime = quiz.getStartTime();
        LocalDateTime quizEndTime = quiz.getEndTime() != null
                ? quiz.getEndTime()
                : (quizStartTime != null && quiz.getDuration() != null ? quizStartTime.plusMinutes(quiz.getDuration()) : null);

        if (quizStartTime == null || quizEndTime == null || currentTime.isBefore(quizStartTime) || currentTime.isAfter(quizEndTime)) {
            throw new IllegalStateException("Quiz is not currently active or has ended");
        }
    }

//    private void validateStudentAccess(UUID quizId, Long studentId) {
//
//        Student student = studentRepository.findById(studentId)
//                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
//
//        Quiz quiz = quizRepository.findById(quizId)
//                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));
//
//        boolean belongs = student.getGroupings().stream()
//                .anyMatch(g -> g.getId().equals(quiz.getGrouping().getId()));
//
//        if (!belongs) {
//            throw new IllegalStateException("Student is not allowed to take this quiz");
//        }
//    }


    @Transactional
    public UpcomingTaskInfo getUpcomingQuizInfo() {
        Long userId = CurrentUserUtils.getUserId();
        Student student = studentRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + userId));
        if (student.getGroupings().isEmpty()) {
            throw new EntityNotFoundException("Student is not assigned to any group");
        }
        List<UUID> courseIds = student.getGroupings().stream()
                .map(Grouping::getCourseId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Quiz quiz = quizRepository.findUpcomingQuizByCourseIds(courseIds, LocalDateTime.now());
        if (quiz == null) {
            return null;
        }

        UpcomingTaskInfo upcomingTaskInfo = new UpcomingTaskInfo();
        upcomingTaskInfo.setTitle(quiz.getTitle());
        upcomingTaskInfo.setTime(quiz.getStartTime());

        return upcomingTaskInfo;
    }

    public Page<QuizSummaryDTO> getQuizzesList(
            UUID courseId,
            String title,
            QuizContentStatus status,
            Pageable pageable
    ) {
        Specification<Quiz> spec = QuizSpecification.advancedFilter(courseId, title, status);

        Pageable effectivePageable = pageable != null
                ? PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().and(Sort.by("startTime").descending())
        )
                : PageRequest.of(0, 10, Sort.by("startTime").descending());

        LocalDateTime now = LocalDateTime.now();

        return quizRepository.findAll(spec, effectivePageable)
                .map(quiz -> toSummaryDTOWithStatus(quiz, now));
    }


    private QuizSummaryDTO toSummaryDTOWithStatus(Quiz quiz, LocalDateTime now) {
        QuizSummaryDTO dto = QuizMapper.toSummaryDTO(quiz);
        if (quiz.getStatus() == Status.INACTIVE) {
            dto.setStatus(QuizContentStatus.CANCELLED);
            return dto;
        }

        LocalDateTime start = quiz.getStartTime();
        LocalDateTime end = quiz.getEndTime() != null
                ? quiz.getEndTime()
                : (start != null && quiz.getDuration() != null ? start.plusMinutes(quiz.getDuration()) : null);

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


    public QuizSummaryDTO getQuiz(UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("No quiz found with this id"));

        LocalDateTime now = LocalDateTime.now();

        return toSummaryDTOWithStatus(quiz, now);
    }
}


//    @Transactional
//    public String submitAndCheckAnswers(List<Response> responseList, Long studentId, UUID quizId) {
//        LocalDateTime currentTime = LocalDateTime.now();
//
//        if (!studentRepository.existsById(studentId)) {
//            throw new EntityNotFoundException("No student found with this id");
//        }
//        Quiz quiz = quizRepository.findById(quizId)
//                .orElseThrow(() -> new EntityNotFoundException("No quiz found with this id"));
//
//        LocalDateTime quizStartTime = quiz.getStartTime();
//        LocalDateTime quizEndTime = quiz.getEndTime();
//
//        if (currentTime.isBefore(quizStartTime) || currentTime.isAfter(quizEndTime)) {
//            throw new IllegalStateException("Quiz is not currently active or has ended");
//        }
//
//        WrongAnswersAnalyze wrongAnswersAnalyze = new WrongAnswersAnalyze();
//        QuizResults quizResults = new QuizResults();
//        long mark = 0L;
//
//        for (Response response : responseList) {
//
//            Question question = questionRepository.findById(response.getQuestion_id()).orElseThrow(() -> new EntityNotFoundException("No question found with this id"));
//
//            if (!question.getType().equals("MCQ")) {
//                WrittenQuestions writtenQuestions = new WrittenQuestions();
//                writtenQuestions.setQuestionId(question.getId());
//                writtenQuestions.setQuizId(quizId);
//                writtenQuestions.setStudentAnswer(response.getAnswer());
//                writtenQuestions.setStudentId(studentId);
//                writtenQuestions.setCorrect_answer(question.getRight_answer());
//                writtenQuestions.setQuestionTitle(question.getTitle());
//                writtenQuestions.setMax_score((long) question.getMark());
//                writtenQuestionsRepository.save(writtenQuestions);
//            } else if (question.getRight_answer().equals(response.getAnswer())) {
//                mark += question.getMark();
//            } else {
//                wrongAnswersAnalyze.setQuestion_id(response.getQuestion_id());
//                wrongAnswersAnalyze.setWrong_answer(response.getAnswer());
//            }
//
//        }
//        wrongAnswersAnalyze.setStudentId(studentId);
//        wrongAnswersAnalyze.setQuiz_id(quizId);
//        wrongAnswersAnalyzeRepository.save(wrongAnswersAnalyze);
//
//        quizResults.setQuiz(quizRepository.findById(quizId).get());
//        quizResults.setMark(mark);
//        quizResults.setStudentId(studentId);
//        quizResults.setCenterId(TenantContext.getCenterId());
//        quizResultsRepository.save(quizResults);
//
//        return "Successfully recorded!";
//    }