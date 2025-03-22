package com.example.demo.management.dto.response;

//import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Schema(description = "Access token")
public class AccessTokenResDto {
//    @Schema(description = "Access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dnZWRJbkFzIjoiYWRtaW4iLCJpYXQiOjE0MjI3Nzk2Mzh9.gzSraSYS8EXBxLN _oWnFSRgCzcmJmMjLiuyu5CSpyHI=")
    private String accessToken;
    private String refreshToken;
}
