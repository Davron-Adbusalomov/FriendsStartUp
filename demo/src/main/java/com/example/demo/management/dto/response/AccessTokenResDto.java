package com.example.demo.management.dto.response;

//import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Schema(description = "Access token")
public class AccessTokenResDto {
    private String accessToken;
    private String refreshToken;
    private UserLoginResponseDto userDetails;
}
