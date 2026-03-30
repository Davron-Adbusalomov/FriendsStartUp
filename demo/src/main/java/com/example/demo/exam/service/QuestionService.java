package com.example.demo.exam.service;

import com.example.demo.config.TenantContext;
import com.example.demo.exam.dto.QuestionSummaryDTO;
import com.example.demo.exam.mapper.QuestionMapper;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.exam.dto.QuestionDTO;
import com.example.demo.exam.model.Option;
import com.example.demo.exam.model.Question;
import com.example.demo.exam.model.Quiz;
import com.example.demo.exam.repository.OptionRepository;
import com.example.demo.exam.repository.QuestionRepository;
import com.example.demo.exam.repository.QuizRepository;
import com.example.demo.management.specification.QuestionSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private MediaService mediaService;

    public QuestionDTO getQuestionById(UUID id){
        Question question = questionRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("No question found with this id: " + id)
        );

        QuestionDTO result = QuestionMapper.toDTO(question);
        result.setRight_answer(question.getRight_answer());
        return result;
    }

    public Page<QuestionSummaryDTO> getAllQuestions(String level, Long teacherId, UUID quizId, String search, UUID subjectId, Pageable pageable) {
        Specification<Question> spec = QuestionSpecification.advancedFilter(level, teacherId, quizId, search, subjectId);

        Page<Question> page = questionRepository.findAll(spec, pageable);

        return page.map(QuestionMapper::toSummaryDTO);
    }


    public String createQuestion(QuestionDTO questionDTO) throws IOException {
        List<Option> arrayList = new ArrayList<>();

            for (int i = 0; i < questionDTO.getOptions().size(); i++) {
                Option option = new Option();
                option.setText(questionDTO.getOptions().get(i));
                arrayList.add(option);
            }
            Optional<Teacher> optionalTeacher = teacherRepository.findById(questionDTO.getTeacherId());

            if (optionalTeacher.isPresent()) {
                BufferedImage img = null;
                if (questionDTO.getImage() != null && !questionDTO.getImage().isEmpty()) {
                    String base64Image = questionDTO.getImage().split(",")[1];
                    byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

                    img = ImageIO.read(new ByteArrayInputStream(imageBytes));
                }

                Teacher teacher = optionalTeacher.get();
                Question question = new Question();
                    question.setLevel(questionDTO.getLevel());
                    question.setSubjectId(questionDTO.getSubjectId());
                    if (img!=null) question.setImage( mediaService.uploadImageToAzureAndGetUrl(img, "question"+ UUID.randomUUID()));
                    question.setTitle(questionDTO.getTitle());
                    question.setType(questionDTO.getType().toUpperCase());
                    question.setMark(questionDTO.getMark());
                    question.setRight_answer(questionDTO.getRight_answer());
                    question.setTeacher(teacher);
                    question.setCenterId(TenantContext.getCenterId());
                    if (questionDTO.getType().equals("MC")){
                        for (Option option:arrayList) {
                            question.assignOption(option);
                        }}

                questionRepository.save(question);

                for (Option option:arrayList) {
                    option.setQuestion(question);
                    option.setCenterId(TenantContext.getCenterId());
                    optionRepository.save(option);
                }

                return "Question created successfully.";
            } else {
                throw new EntityNotFoundException("No teacher found with this id!");
            }
    }

    public ResponseEntity<?> deleteQuestion(UUID questionId){
        Question question = questionRepository.findById(questionId)
                .orElseThrow(()-> new EntityNotFoundException("Question not found with this id:" + questionId));

        List<Quiz> quizzes = quizRepository.findByQuestionId(questionId);

        for (Quiz quiz: quizzes) {
            quiz.getQuestions().remove(question);
        }

        questionRepository.delete(question);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted");
    }


    public ResponseEntity<?> updateQuestion(QuestionDTO questionDTO,UUID id){
        Question updatedQuestion = questionRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("No question found with this id: " + id)
        );

        if (questionDTO.getMark() != null) {
            updatedQuestion.setMark(questionDTO.getMark());
        }
        if (questionDTO.getType() != null) {
            updatedQuestion.setType(questionDTO.getType());
        }
        if (questionDTO.getRight_answer() != null) {
            updatedQuestion.setRight_answer(questionDTO.getRight_answer());
        }
        if (questionDTO.getTitle() != null) {
            updatedQuestion.setTitle(questionDTO.getTitle());
        }
        if (questionDTO.getLevel() != null) {
            updatedQuestion.setLevel(questionDTO.getLevel());
        }
        if (questionDTO.getSubjectId() != null) {
            updatedQuestion.setSubjectId(questionDTO.getSubjectId());
        }
        if (questionDTO.getImage() != null) {
            updatedQuestion.setImage(questionDTO.getImage());
        }

        Question saved = questionRepository.save(updatedQuestion);
        QuestionDTO result = QuestionMapper.toDTO(saved);
        result.setRight_answer(saved.getRight_answer());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public List<Question> getQuestionByLevel(String level) {
        return questionRepository.findQuestionByLevel(level);
    }
}
