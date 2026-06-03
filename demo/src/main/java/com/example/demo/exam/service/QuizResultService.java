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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

    TelegramConfig telegramConfig = new TelegramConfig(this);

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

        int maxMark = quiz.getQuestions().stream()
                .mapToInt(q -> q.getMark() != null ? q.getMark() : 0)
                .sum();
        if (maxMark == 0) maxMark = 1;

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

            long mark       = ranking.getTotalMark() != null ? ranking.getTotalMark() : 0L;
            long place      = Long.parseLong(ranking.getRank());
            double percent  = mark * 100.0 / maxMark;

            String text = String.format(
                "📊 Assalomu alaykum!\n\n" +
                "Farzandingiz *%s* ning «%s» sinov natijalari:\n\n" +
                "✅ To'plagan ball: %d / %d\n" +
                "📈 O'zlashtirish: %.1f%%\n" +
                "🏆 Guruhda o'rni: %d / %d\n",
                student.getFullName(),
                quiz.getTitle(),
                mark,
                maxMark,
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
        if (text.equals("/start")){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            System.out.println(chatId);
            sendMessage.setText("Assalomu alaykum. Xush kelibsiz! Iltimos, ro'yxatdan o'tish uchun telefon raqamingizni kiriting:\n(Masalan:934983233)!");
            telegramConfig.execute(sendMessage);
        }
        else {
            Optional<Student> student = studentRepository.findStudentByParentContact(text);
            if (student.isEmpty()){
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Noto'gri raqam kiritdingiz! Raqamingiz administrator tomonidan ro'yxatga olinishi kerak. Iltimos administrator bilan boglaning!");
                telegramConfig.execute(sendMessage);
            }
            else {
                Student updateStudent = student.get();
                updateStudent.setParentChatId(String.valueOf(chatId));
                studentRepository.save(updateStudent);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Siz muvaffaqqiyatli ro'yxatdan o'tdingiz! Iltimos bizdan uzoqlashmang. Farzandingizning exam natijalari haqida sizga xabar berib boramiz!");
                telegramConfig.execute(sendMessage);
            }
        }
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

        QuizResults qR = new QuizResults();
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
