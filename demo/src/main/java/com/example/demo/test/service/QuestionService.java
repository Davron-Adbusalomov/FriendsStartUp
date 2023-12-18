package com.example.demo.test.service;

import com.example.demo.test.dto.QuestionDTO;
import com.example.demo.test.model.Question;
import com.example.demo.test.repository.QuestionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public QuestionDTO createQuestion(QuestionDTO questionDto) {
        Question question = new Question();
        BeanUtils.copyProperties(questionDto, question);

        // Set additional properties before saving
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());

        Question savedQuestion = questionRepository.save(question);

        QuestionDTO savedQuestionDto = new QuestionDTO();
        BeanUtils.copyProperties(savedQuestion, savedQuestionDto);

        return savedQuestionDto;
    }

    public List<QuestionDTO> getAllQuestions() {
        List<Question> questions = questionRepository.findAll();
        return questions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<QuestionDTO> getQuestionById(Long id) {
        Optional<Question> questionOptional = questionRepository.findById(id);
        return questionOptional.map(this::convertToDto);
    }

    public QuestionDTO updateQuestion(Long id, QuestionDTO updatedQuestionDto) {
        Optional<Question> existingQuestionOptional = questionRepository.findById(id);

        if (existingQuestionOptional.isPresent()) {
            Question existingQuestion = existingQuestionOptional.get();
            BeanUtils.copyProperties(updatedQuestionDto, existingQuestion);

            // Update additional fields as needed
            existingQuestion.setUpdatedAt(LocalDateTime.now());

            Question updatedQuestion = questionRepository.save(existingQuestion);

            return convertToDto(updatedQuestion);
        } else {
            // Handle the case where the question with the given ID is not found
            return null;
        }
    }

    public boolean deleteQuestion(Long id) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isPresent()) {
            questionRepository.deleteById(id);
            return true;
        } else {
            return false;
        }

    }

    private QuestionDTO convertToDto(Question question) {
        QuestionDTO questionDto = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDto);
        // Additional conversion logic if needed
        return questionDto;
    }
}
