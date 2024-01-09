package com.example.demo;

import com.example.demo.config.TelegramConfig;
import com.example.demo.test.service.QuizResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class StartupApplication {

	private final QuizResultService quizResultService;
	private final TelegramConfig telegramConfig;

	@Autowired
	public StartupApplication(QuizResultService quizResultService, TelegramConfig telegramConfig) {
		this.quizResultService = quizResultService;
		this.telegramConfig = telegramConfig;
	}

	public static void main(String[] args) throws TelegramApiException {
		ConfigurableApplicationContext appContext = SpringApplication.run(StartupApplication.class, args);
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		StartupApplication application = appContext.getBean(StartupApplication.class);
		telegramBotsApi.registerBot(application.telegramConfig);
		System.out.println("Hello Team");
	}
}

