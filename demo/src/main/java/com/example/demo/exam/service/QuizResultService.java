package com.example.demo.exam.service;

import com.example.demo.config.TelegramConfig;
import com.example.demo.exam.model.*;
import com.example.demo.exam.repository.*;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.exam.dto.WrittenQuestionsResponseDTO;
import com.example.demo.exam.dto.Quiz_ResultsDTO;
import com.example.demo.exam.dto.WrittenAnswerDTO;
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

//    public List<WrittenAnswerDTO> getWrittenQuestions(String groupName, UUID quizId){
//        Optional<Grouping> grouping = groupRepository.findByName(groupName);
//        if (grouping.isEmpty()){
//            throw new EntityNotFoundException("No group found with this id");
//        }
//
//        List<WrittenQuestions> writtenQuestions = writtenQuestionsRepository.findByQuizId(quizId);
//        List<WrittenAnswerDTO> writtenAnswerDTOS = new ArrayList<>();
//
//        for (WrittenQuestions w:writtenQuestions) {
//            WrittenAnswerDTO writtenAnswerDTO = new WrittenAnswerDTO();
//            writtenAnswerDTO.setId(w.getId());
//            writtenAnswerDTO.setScore(w.getScore());
//            writtenAnswerDTO.setQuizId(w.getQuizId());
//            writtenAnswerDTO.setStudentAnswer(w.getStudentAnswer());
//            writtenAnswerDTO.setCorrectAnswer(w.getCorrect_answer());
//            writtenAnswerDTO.setStudentId(w.getStudent().getId());
//            writtenAnswerDTO.setQuestionTitle(w.getQuestionTitle());
//            writtenAnswerDTO.setMax_score(w.getMax_score());
//            writtenAnswerDTOS.add(writtenAnswerDTO);
//        }
//
//        return writtenAnswerDTOS;
//    }

    public void assignQuizResult(List<WrittenQuestionsResponseDTO> writtenQuestionsResponseDTO) {
        for (WrittenQuestionsResponseDTO w:writtenQuestionsResponseDTO) {
            Optional<WrittenQuestions> writtenQuestion = writtenQuestionsRepository.findById(w.getId());
            if (writtenQuestion.isEmpty()){
                throw new EntityNotFoundException("Not found");
            }

            Long studentId = writtenQuestion.get().getStudent().getId();
            UUID quizId = writtenQuestion.get().getQuizId();

            QuizResults quizResult = quizResultsRepository.findByStudentIdAndQuizId(studentId, quizId);

            quizResult.setMark(quizResult.getMark()+w.getMark());

            quizResultsRepository.save(quizResult);
        }

//        quizResults.setMark(writtenQuestionsResponseDTO.getMark());
//
//        quizResultsRepository.save(quizResults);
    }

    public void finalizeQuiz(UUID quizId) throws TelegramApiException {
        Optional<Quiz> quiz = quizRepository.findById(quizId);

        int maxMark = 0;

        for (Question question : quiz.get().getQuestions()) {
            maxMark += question.getMark();
        }

        for (Student student : quiz.get().getGrouping().getStudents()) {
            QuizResults quizResults = student.getQuizResults().get(student.getQuizResults().size() - 1);

            int place = 1;

            for (int i = 0; i < quiz.get().getGrouping().getStudents().size(); i++) {
                Student otherStudent = quiz.get().getGrouping().getStudents().get(i);
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
            sendMessage.setText("Assalomu alaykum! Farzandingiz, "+ student.getFullName()+" " + quiz.get().getGrouping().getSubjectId() + " fanidan oxirgi sinov natijasi bilan tanishing:\n o'zlashtirish foizi: " + (quizResults.getMark() * 100.0) / maxMark +"%\n guruhdagi o'rni: " + place + "-o'rin\n ");

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
        }

        studentAnswerRepository.saveAll(answers);

        QuizResults qR = new QuizResults();
        qR.setStudentId(studentId);
        qR.setQuizId(quizId);
        qR.setMark(totalScore);

        quizResultsRepository.save(qR);
    }

}
