package com.example.demo.test.service;

import com.example.demo.test.dto.OptionDTO;
import com.example.demo.test.mapper.OptionMapper;
import com.example.demo.test.model.Option;
import com.example.demo.test.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OptionService {

    private final OptionRepository optionRepository;

    @Autowired
    public OptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    public OptionDTO getOptionById(Long id) {
        Optional<Option> optionOptional = optionRepository.findById(id);
        return optionOptional.map(OptionMapper.INSTANCE::optionToOptionDTO).orElse(null);
    }

    public OptionDTO saveOption(OptionDTO optionDTO) {
        Option option = OptionMapper.INSTANCE.optionDTOtoOption(optionDTO);
        Option savedOption = optionRepository.save(option);
        return OptionMapper.INSTANCE.optionToOptionDTO(savedOption);
    }

    public OptionDTO updateOption(Long id, OptionDTO optionDTO) {
        Optional<Option> existingOptionOptional = optionRepository.findById(id);
        if (existingOptionOptional.isPresent()) {
            Option existingOption = existingOptionOptional.get();

            // Update the existing Option entity with data from the DTO
            OptionMapper.INSTANCE.updateOptionFromDTO(optionDTO, existingOption);

            // Save the updated Option entity
            Option updatedOption = optionRepository.save(existingOption);

            // Map and return the updated Option as DTO
            return OptionMapper.INSTANCE.optionToOptionDTO(updatedOption);
        } else {
            // Handle not found scenario, you can throw an exception or return null/error DTO
            return null;
        }
    }

    public void deleteOption(Long id) {
        optionRepository.deleteById(id);
    }

    public List<OptionDTO> getAllOptions() {
        List<Option> options = optionRepository.findAll();
        return options.stream()
                .map(OptionMapper.INSTANCE::optionToOptionDTO)
                .collect(Collectors.toList());
    }
}