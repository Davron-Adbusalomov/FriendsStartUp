package com.example.demo.test.service;

import com.example.demo.test.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OptionService {
    @Autowired
    private OptionRepository optionRepository;
}
