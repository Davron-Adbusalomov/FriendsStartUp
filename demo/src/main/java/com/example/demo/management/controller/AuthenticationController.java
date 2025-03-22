package com.example.demo.management.controller;

import com.example.demo.management.authentication.service.AuthenticationService;
import com.example.demo.management.dto.request.RefreshTokenRequest;
import com.example.demo.management.dto.request.SignInReqDto;
import com.example.demo.management.dto.request.SignUpRequest;
import com.example.demo.management.dto.response.AccessTokenResDto;
import com.example.demo.management.dto.response.SignUpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Operation(
            summary = "Sign in to the application",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully signed in", content = @Content(schema = @Schema(implementation = AccessTokenResDto.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
            })
    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.CREATED)
    public AccessTokenResDto login(@RequestBody @Valid SignInReqDto signInDto, HttpServletRequest request){
        logger.info("Sign-in request: {}", signInDto);
        return authenticationService.signIn(signInDto, request);
    }

    @Operation(
            summary = "Sign up to the application",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully signed up", content = @Content(schema = @Schema(implementation = SignUpResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Conflict - Username already exists", content = @Content)
            })
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public SignUpResponse signUp(@RequestBody @Valid SignUpRequest request) {
        logger.info("Sign-up request: {}", request);
        return authenticationService.signUp(request);
    }

    @Operation(
            summary = "Refresh access token using refresh token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully", content = @Content(schema = @Schema(implementation = AccessTokenResDto.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired refresh token", content = @Content)
            })
    @PostMapping("/refresh-token")
    public AccessTokenResDto refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        logger.info("Refresh token request: {}", request);
        return authenticationService.refreshToken(request);
    }
}
