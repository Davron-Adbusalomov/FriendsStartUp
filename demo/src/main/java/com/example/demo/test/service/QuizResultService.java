package com.example.demo.test.service;

import com.example.demo.config.TelegramConfig;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.test.dto.WrittenQuestionsResponseDTO;
import com.example.demo.test.dto.Quiz_ResultsDTO;
import com.example.demo.test.dto.WrittenQuestionsDTO;
import com.example.demo.test.model.Question;
import com.example.demo.test.model.Quiz;
import com.example.demo.test.model.Quiz_Results;
import com.example.demo.test.model.WrittenQuestions;
import com.example.demo.test.repository.QuizRepository;
import com.example.demo.test.repository.Quiz_ResultsRepository;
import com.example.demo.test.repository.WrittenQuestionsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizResultService {
    @Autowired
    private Quiz_ResultsRepository quizResultsRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private WrittenQuestionsRepository writtenQuestionsRepository;

    TelegramConfig telegramConfig = new TelegramConfig(this);

    public Quiz_ResultsDTO getQuizResult(){
        return null;
    }

    public List<WrittenQuestionsDTO> getWrittenQuestions(String groupName, Long quizId){
        Optional<Grouping> grouping = groupRepository.findByName(groupName);
        if (grouping.isEmpty()){
            throw new EntityNotFoundException("No group found with this id");
        }

        List<WrittenQuestions> writtenQuestions = writtenQuestionsRepository.findByQuizId(quizId);
        List<WrittenQuestionsDTO> writtenQuestionsDTOS = new ArrayList<>();

        for (WrittenQuestions w:writtenQuestions) {
            WrittenQuestionsDTO writtenQuestionsDTO = new WrittenQuestionsDTO();
            writtenQuestionsDTO.setId(w.getId());
            writtenQuestionsDTO.setScore(w.getScore());
            writtenQuestionsDTO.setQuizId(w.getQuizId());
            writtenQuestionsDTO.setStudentAnswer(w.getStudentAnswer());
            writtenQuestionsDTO.setCorrect_answer(w.getCorrect_answer());
            writtenQuestionsDTO.setStudentId(w.getStudent().getId());
            writtenQuestionsDTO.setQuestionTitle(w.getQuestionTitle());
            writtenQuestionsDTO.setMax_score(w.getMax_score());
            writtenQuestionsDTOS.add(writtenQuestionsDTO);
        }

        return writtenQuestionsDTOS;
    }

    public void assignQuizResult(List<WrittenQuestionsResponseDTO> writtenQuestionsResponseDTO) {
        for (WrittenQuestionsResponseDTO w:writtenQuestionsResponseDTO) {
            Optional<WrittenQuestions> writtenQuestion = writtenQuestionsRepository.findById(w.getId());
            if (writtenQuestion.isEmpty()){
                throw new EntityNotFoundException("Not found");
            }

            Long studentId = writtenQuestion.get().getStudent().getId();
            Long quizId = writtenQuestion.get().getQuizId();

            Quiz_Results quizResult = quizResultsRepository.findByStudentIdAndQuizId(studentId, quizId);

            quizResult.setMark(quizResult.getMark()+w.getMark());

            quizResultsRepository.save(quizResult);
        }

//        quizResults.setMark(writtenQuestionsResponseDTO.getMark());
//
//        quizResultsRepository.save(quizResults);
    }

    public void finalizeQuiz(Long quizId) throws TelegramApiException {
        Optional<Quiz> quiz = quizRepository.findById(quizId);

        int maxMark = 0;

        for (Question question:quiz.get().getQuestions()) {
            maxMark+=question.getMark();
        }

        for (Student student:quiz.get().getGrouping().getStudents()) {
            Quiz_Results quizResults = student.getQuizResults().get(student.getQuizResults().size()-1);

            int place=1;

            for (int i=0; i<quiz.get().getGrouping().getStudents().size(); i++){
                Student otherStudent = quiz.get().getGrouping().getStudents().get(i);
                List<Quiz_Results> otherStudentQuizResults = otherStudent.getQuizResults();

                Quiz_Results otherStudentLastResult = otherStudentQuizResults.get(otherStudentQuizResults.size() - 1);
                if (quizId.equals(otherStudentLastResult.getQuiz().getId()) &&
                        quizResults.getMark() < otherStudentLastResult.getMark()){
                    place++;
                }
            }

            if (student.getParent_chatId().isEmpty()){
                continue;
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
