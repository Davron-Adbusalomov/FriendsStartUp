package com.example.demo.exam.service;

import com.example.demo.config.TelegramConfig;
import com.example.demo.config.TenantContext;
import com.example.demo.exam.dto.WrittenQuestionsEvaluateDTO;
import com.example.demo.exam.interfaces.StudentRanking;
import com.example.demo.exam.model.*;
import com.example.demo.exam.repository.*;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.exam.dto.Quiz_ResultsDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizResultService {
    private final QuizResultsRepository quizResultsRepository;

    private final QuizRepository quizRepository;

    private final StudentRepository studentRepository;

    private final WrittenQuestionsRepository writtenQuestionsRepository;

    private final StudentAnswerRepository studentAnswerRepository;

    private final QuestionRepository questionRepository;

    @Lazy
    @Autowired
    private TelegramConfig telegramConfig;

    public Quiz_ResultsDTO getQuizResult(){
        return null;
    }

    @Transactional
    public void assignQuizResult(Long studentId, UUID quizId, List<WrittenQuestionsEvaluateDTO> writtenQuestionsEvaluateDTO) {
        QuizResults quizResults = quizResultsRepository.findByStudentIdAndQuizId(studentId, quizId).orElse(new QuizResults());
        for (WrittenQuestionsEvaluateDTO w: writtenQuestionsEvaluateDTO) {
            StudentAnswer answer = studentAnswerRepository
                    .findByStudentIdAndQuizIdAndQuestionId(
                            studentId, quizId, w.getQuestionId()
                    )
                    .orElseThrow(() -> new EntityNotFoundException("No answer found for student " + studentId + " and question " + w.getQuestionId()));
            answer.setScore(w.getMark());
            answer.setCorrect(w.getMark() > 0);
            answer.setCenterId(TenantContext.getCenterId());
            studentAnswerRepository.save(answer);

            long current = quizResults.getMark() != null ? quizResults.getMark() : 0L;
            quizResults.setMark(current + w.getMark());
        }
        quizResults.setQuizId(quizId);
        quizResults.setStudentId(studentId);
        quizResults.setCenterId(TenantContext.getCenterId());
        quizResultsRepository.save(quizResults);
    }


    public void finalizeQuiz(UUID quizId, UUID groupingId) throws TelegramApiException {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + quizId));

        // Mark quiz as ended so status becomes COMPLETED
        quiz.setEndTime(LocalDateTime.now());
        quizRepository.save(quiz);

        // findRankings handles null groupingId — filters by quiz only in that case
        List<StudentRanking> rankings = quizResultsRepository.findRankings(groupingId, quizId);
        int totalStudents = rankings.size();

        for (StudentRanking ranking : rankings) {
            Student student = studentRepository.findById(ranking.getStudentId()).orElse(null);
            if (student == null
                    || student.getParentChatId() == null
                    || student.getParentChatId().isBlank()) {
                continue;
            }

            // Read mark directly from the DB record — avoids JOIN multiplication in findRankings
            QuizResults result = quizResultsRepository
                    .findByStudentIdAndQuizId(ranking.getStudentId(), quizId)
                    .orElse(null);
            if (result == null) continue;

            long mark = result.getMark() != null ? result.getMark() : 0L;

            // maxMark = sum of marks for questions this student actually received
            int studentMaxMark = studentAnswerRepository
                    .findByQuizIdAndStudentId(quizId, ranking.getStudentId())
                    .stream()
                    .mapToInt(a -> questionRepository.findById(a.getQuestionId())
                            .map(q -> q.getMark() != null ? q.getMark() : 0)
                            .orElse(0))
                    .sum();
            if (studentMaxMark == 0) studentMaxMark = 1;

            long place     = Long.parseLong(ranking.getRank());
            double percent = Math.min(mark * 100.0 / studentMaxMark, 100.0);

            String text = String.format(
                "📊 Assalomu alaykum!\n\n" +
                "Farzandingiz *%s* ning «%s» sinov natijalari:\n\n" +
                "✅ To'plagan ball: %d / %d\n" +
                "📈 O'zlashtirish: %.1f%%\n" +
                "🏆 Guruhda o'rni: %d / %d\n",
                student.getFullName(),
                quiz.getTitle(),
                mark,
                studentMaxMark,
                percent,
                place,
                totalStudents
            );

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(student.getParentChatId());
            sendMessage.setText(text);
            sendMessage.enableMarkdown(true);
            telegramConfig.execute(sendMessage);
        }
    }

    public void onUpdateReceived(Long chatId, String text) throws TelegramApiException {
        if (text == null) return;

        if (text.startsWith("/start")) {
            String[] parts = text.trim().split(" ", 2);

            if (parts.length == 2 && !parts[1].isBlank()) {
                // Deep link: /start <studentId>
                try {
                    Long studentId = Long.parseLong(parts[1].trim());
                    Optional<Student> studentOpt = studentRepository.findById(studentId);
                    SendMessage reply = new SendMessage();
                    reply.setChatId(String.valueOf(chatId));

                    if (studentOpt.isPresent()) {
                        Student student = studentOpt.get();
                        student.setParentChatId(String.valueOf(chatId));
                        studentRepository.save(student);
                        reply.setText(
                            "✅ Assalomu alaykum!\n\n" +
                            "Siz muvaffaqiyatli ro'yxatdan o'tdingiz!\n\n" +
                            "👦 Farzandingiz: " + student.getFullName() + "\n\n" +
                            "Bundan buyon farzandingizning sinov natijalari haqida xabar olib turasiz! 📊"
                        );
                    } else {
                        reply.setText("❌ Noto'g'ri havola! Iltimos administrator bilan bog'laning.");
                    }
                    telegramConfig.execute(reply);
                } catch (NumberFormatException e) {
                    sendWelcome(chatId);
                }
            } else {
                sendWelcome(chatId);
            }
        }
    }

    private void sendWelcome(Long chatId) throws TelegramApiException {
        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText(
            "Assalomu alaykum! Xush kelibsiz! 👋\n\n" +
            "Ro'yxatdan o'tish uchun administrator tomonidan yuborilgan havoladan foydalaning."
        );
        telegramConfig.execute(msg);
    }

    @Transactional
    public void evaluateQuiz(UUID quizId, Long studentId) {

        List<StudentAnswer> answers =
                studentAnswerRepository.findByQuizIdAndStudentId(quizId, studentId);

        Map<UUID, Question> questions =
                questionRepository.findAllById(
                        answers.stream().map(StudentAnswer::getQuestionId).toList()
                ).stream().collect(Collectors.toMap(Question::getId, q -> q));

        long totalScore = 0;

        for (StudentAnswer a : answers) {
            Question q = questions.get(a.getQuestionId());

            if ("MCQ".equals(q.getType())) {
                boolean correct = q.getRight_answer().equals(a.getAnswer());
                a.setCorrect(correct);
                a.setScore(correct ? q.getMark() : 0);
                totalScore += a.getScore();
            }
            a.setCenterId(TenantContext.getCenterId());
        }

        studentAnswerRepository.saveAll(answers);

        QuizResults qR = quizResultsRepository
                .findByStudentIdAndQuizId(studentId, quizId)
                .orElse(new QuizResults());
        qR.setStudentId(studentId);
        qR.setQuizId(quizId);
        qR.setMark(totalScore);
        qR.setCenterId(TenantContext.getCenterId());

        quizResultsRepository.save(qR);
    }


    public List<StudentRanking> getRankings(UUID groupingId, UUID quizId) {
        return quizResultsRepository.findRankings(groupingId, quizId);
    }
}
