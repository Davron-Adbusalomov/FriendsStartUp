package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminLoginDTO {
    private AdminDTO user;

    private String token;

}
