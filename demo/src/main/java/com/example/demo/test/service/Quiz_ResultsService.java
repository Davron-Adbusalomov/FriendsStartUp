package com.example.demo.test.service;

import com.example.demo.test.repository.Quiz_ResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Quiz_ResultsService {
    @Autowired
    private Quiz_ResultsRepository quizResultsRepository;

}
