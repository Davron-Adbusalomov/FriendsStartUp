package com.example.demo.config;

import com.example.demo.management.model.Student;
import com.example.demo.test.service.QuizResultService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class TelegramConfig extends TelegramLongPollingBot {

    private final QuizResultService quizResultsService;

    public TelegramConfig(QuizResultService quizResultsService) {
        this.quizResultsService = quizResultsService;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        quizResultsService.onUpdateReceived(chatId, text);
    }

    @Override
    public String getBotUsername() {
        return "t.me/admissions2023_bot";
    }

    @Override
    public String getBotToken(){
        return "6452390914:AAGo8c8y8Gq9oG3ihbZcmpAJWeW1ScCwBxY";
    }

}
