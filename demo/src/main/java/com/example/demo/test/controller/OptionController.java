package com.example.demo.test.controller;

import com.example.demo.test.dto.OptionDTO;
import com.example.demo.test.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/options")
public class OptionController {

    private final OptionService optionService;

    @Autowired
    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptionDTO> getOptionById(@PathVariable Long id) {
        OptionDTO optionDTO = optionService.getOptionById(id);
        return optionDTO != null ? ResponseEntity.ok(optionDTO) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<OptionDTO> saveOption(@RequestBody OptionDTO optionDTO) {
        OptionDTO savedOptionDTO = optionService.saveOption(optionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOptionDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptionDTO> updateOption(@PathVariable Long id, @RequestBody OptionDTO optionDTO) {
        OptionDTO updatedOptionDTO = optionService.updateOption(id, optionDTO);
        return updatedOptionDTO != null ? ResponseEntity.ok(updatedOptionDTO) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long id) {
        optionService.deleteOption(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<OptionDTO>> getAllOptions() {
        List<OptionDTO> optionDTOs = optionService.getAllOptions();
        return ResponseEntity.ok(optionDTOs);
    }

    // Add other methods for more advanced operations

}
