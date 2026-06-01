package com.example.demo.exam.service;

import com.example.demo.config.TelegramConfig;
import com.example.demo.config.TenantContext;
import com.example.demo.exam.dto.WrittenQuestionsEvaluateDTO;
import com.example.demo.exam.interfaces.StudentRanking;
import com.example.demo.exam.model.*;
import com.example.demo.exam.repository.*;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.GroupRepository;
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

    private final GroupRepository groupRepository;

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
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + quizId));
        Grouping grouping = groupRepository.findById(groupingId).orElseThrow(() -> new EntityNotFoundException("Grouping not found with id: " + groupingId));

        int maxMark = 0;

        for (Question question : quiz.getQuestions()) {
            maxMark += question.getMark();
        }

        for (Student student : grouping.getStudents()) {
            QuizResults quizResults = student.getQuizResults().get(student.getQuizResults().size() - 1);

            int place = 1;

            for (int i = 0; i < grouping.getStudents().size(); i++) {
                Student otherStudent = grouping.getStudents().get(i);
                List<QuizResults> otherStudentQuizResults = otherStudent.getQuizResults();

                QuizResults otherStudentLastResult = otherStudentQuizResults.get(otherStudentQuizResults.size() - 1);
                if (quizId.equals(otherStudentLastResult.getQuiz().getId()) &&
                        quizResults.getMark() < otherStudentLastResult.getMark()) {
                    place++;
                }
            }

            if (student.getParentChatId().isEmpty()) {
                continue;
            }

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(student.getParentChatId());
            sendMessage.setText("Assalomu alaykum! Farzandingiz, "+ student.getFullName()+" " + grouping.getSubjectId() + " fanidan oxirgi sinov natijasi bilan tanishing:\n o'zlashtirish foizi: " + (quizResults.getMark() * 100.0) / maxMark +"%\n guruhdagi o'rni: " + place + "-o'rin\n ");

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
