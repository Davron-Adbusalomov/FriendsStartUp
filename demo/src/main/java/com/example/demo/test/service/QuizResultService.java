package com.example.demo.test.service;

import com.example.demo.config.TelegramConfig;
import com.example.demo.config.TelegramUsers;
import com.example.demo.config.UserState;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.test.dto.CheckingQuizDTO;
import com.example.demo.test.dto.Quiz_ResultsDTO;
import com.example.demo.test.model.Question;
import com.example.demo.test.model.Quiz;
import com.example.demo.test.model.Quiz_Results;
import com.example.demo.test.repository.QuizRepository;
import com.example.demo.test.repository.Quiz_ResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class QuizResultService {
    @Autowired
    private Quiz_ResultsRepository quizResultsRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private StudentRepository studentRepository;

    TelegramConfig telegramConfig = new TelegramConfig(this);

    public Quiz_ResultsDTO getQuizResult(){
        return null;
    }

    public void assignQuizResult(CheckingQuizDTO checkingQuizDTO, Long quizId) {
        Optional<Quiz> quiz = quizRepository.findById(quizId);

        Quiz_Results quizResults = new Quiz_Results();
        quizResults.setMark(checkingQuizDTO.getCurrent_mark());
        quizResults.assignQuiz(quiz.get());
        quizResults.assignStudent(studentRepository.findById(checkingQuizDTO.getStudent_id()).get());

        quizResultsRepository.save(quizResults);
    }

    public void finalizeQuiz(Long quizId) throws TelegramApiException {
        Optional<Quiz> quiz = quizRepository.findById(quizId);

        int maxMark = 0;
        int place=1;

        for (Question question:quiz.get().getQuestions()) {
            maxMark+=question.getMark();
        }

        for (Student student:quiz.get().getGrouping().getStudents()) {
            Quiz_Results quizResults = student.getQuizResults().get(student.getQuizResults().size()-1);

            for (int i=0; i<quiz.get().getGrouping().getStudents().size()-1; i++){
                Student otherStudent = quiz.get().getGrouping().getStudents().get(i);
                if (quizResults.getMark()<otherStudent.getQuizResults().get(otherStudent.getQuizResults().size()-1).getMark()){
                    place++;
                }
            }

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(student.getParent_chatId());
            sendMessage.setText("Assalomu alaykum! Farzandingiz, "+ student.getName()+" " + quiz.get().getGrouping().getSubject() + " fanidan oxirgi sinov natijasi bilan tanishing:\n o'zlashtirish foizi: " + (quizResults.getMark() * 100.0) / maxMark +"%\n guruhdagi o'rni: " + place + "-o'rin\n ");
            telegramConfig.execute(sendMessage);
        }
    }

    public void onUpdateReceived(Long chatId, String text) throws TelegramApiException {
        if (text.equals("/start")){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
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
                updateStudent.setParent_chatId(String.valueOf(chatId));
                studentRepository.save(updateStudent);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Siz muvaffaqqiyatli ro'yxatdan o'tdingiz! Iltimos bizdan uzoqlashmang. Farzandingizning test natijalari haqida sizga xabar berib boramiz!");
                telegramConfig.execute(sendMessage);
            }
        }
    }
}
