//package com.example.demo.test.controller;
//
//import com.example.demo.test.dto.OptionDTO;
//import com.example.demo.test.service.OptionService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(OptionController.class)
//@ExtendWith(MockitoExtension.class)
//class OptionControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private OptionController optionController;
//
//    @Mock
//    private OptionService optionService;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeEach
//    void setUp() {
//        this.mockMvc = MockMvcBuilders.standaloneSetup(optionController).build();
//    }
//
//    @Test
//    void getOptionById_shouldReturnOptionDTO_whenOptionExists() throws Exception {
//        Long optionId = 1L;
//        OptionDTO optionDTO = new OptionDTO();
//        optionDTO.setId(optionId);
//        optionDTO.setText("Option Text");
//        optionDTO.setQuestionId(2L);
//
//        when(optionService.getOptionById(optionId)).thenReturn(optionDTO);
//
//        mockMvc.perform(get("/api/options/{id}", optionId))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(optionId))
//                .andExpect(jsonPath("$.text").value("Option Text"))
//                .andExpect(jsonPath("$.questionId").value(2L));
//    }
//
//    // ... (Other test methods)
//}
