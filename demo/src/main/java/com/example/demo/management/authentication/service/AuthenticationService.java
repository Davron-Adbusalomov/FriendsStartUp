package com.example.demo.management.authentication.service;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.enums.PermissionEnum;
import com.example.demo.exception.InvalidLoginRequestException;
import com.example.demo.exception.InvalidRefreshTokenException;
import com.example.demo.management.authentication.enums.RolesEnum;
import com.example.demo.management.dto.RoleDto;
import com.example.demo.management.dto.request.RefreshTokenRequest;
import com.example.demo.management.dto.request.SignInReqDto;
import com.example.demo.management.dto.request.SignUpRequest;
import com.example.demo.management.dto.response.AccessTokenResDto;
import com.example.demo.management.dto.response.SignUpResponse;
import com.example.demo.management.mapper.RoleMapper;
import com.example.demo.management.model.Student;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.model.UserEntity;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.management.repository.UserRepository;
import com.example.demo.management.security.dto.DefaultPermissionsDto;
import com.example.demo.utils.ProjectUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public AccessTokenResDto signIn(SignInReqDto signInDto, HttpServletRequest request) {
        Authentication authentication = authenticateUser(signInDto, request);
        return generateTokens(authentication);
    }

    public AccessTokenResDto refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userName = jwtTokenProvider.extractUsername(refreshTokenRequest.getRefreshToken());
        if (!jwtTokenProvider.isTokenValid(refreshTokenRequest.getRefreshToken(), userName)) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        AccessTokenResDto accessTokenResDto = new AccessTokenResDto();
        accessTokenResDto.setRefreshToken(refreshTokenRequest.getRefreshToken());
        accessTokenResDto.setAccessToken(jwtTokenProvider.generateTokenWitClaims(refreshTokenRequest.getRefreshToken(), false));

        return accessTokenResDto;
    }

    public SignUpResponse signUp(SignUpRequest request) {
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            validateUniqueUsername(request.getUsername());
        }

        UserEntity entity = createUserEntity(request);
        entity = userRepository.save(entity);

        if (entity.getRoles().stream().anyMatch(e -> e.getName().equals(RolesEnum.STUDENT))) {
            if (studentRepository.findById(entity.getId()).isEmpty()){
                Student student = new Student();
                student.setId(entity.getId());
                student.setName(entity.getFullName());
                studentRepository.save(student);
            }
        }
        if (entity.getRoles().stream().anyMatch(e -> e.getName().equals(RolesEnum.TEACHER))) {
            if (teacherRepository.findById(entity.getId()).isEmpty()){
                Teacher teacher = new Teacher();
                teacher.setId(entity.getId());
                teacher.setName(entity.getFullName());
                teacherRepository.save(teacher);
            }
        }
        return buildSignUpResponse(entity);
    }

    private Authentication authenticateUser(SignInReqDto signInDto, HttpServletRequest request) {
        Authentication authentication;
        if ((signInDto.getUsername() != null && signInDto.getPassword() != null)) {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInDto.getUsername(), signInDto.getPassword())
            );
        } else {
            throw new InvalidLoginRequestException("Invalid login request");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    private AccessTokenResDto generateTokens(Authentication authentication) {
        AccessTokenResDto response = new AccessTokenResDto();
        response.setAccessToken(jwtTokenProvider.generateToken(authentication, false));
        response.setRefreshToken(jwtTokenProvider.generateToken(authentication, true));
        return response;
    }

    private void validateUniqueUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new DataIntegrityViolationException("This username already exists");
        }
    }

    private UserEntity createUserEntity(SignUpRequest request) {
        UserEntity entity = new UserEntity();
        entity.setUsername(request.getUsername());

        long id = ProjectUtils.getCurrentUserDetails().getId();
        entity.setCreatedBy(id);
        entity.setUpdatedBy(id);
        entity.setCreatedAt(LocalDateTime.now());

        if (request.getPassword() != null) {
            entity.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() == null) {
            RoleDto roleDto = new RoleDto();
            roleDto.setName(RolesEnum.ROLE_USER);
            roleDto.setPrivilege(10);

            DefaultPermissionsDto defaultPermission = new DefaultPermissionsDto();
            defaultPermission.setName(PermissionEnum.CREATE);
            roleDto.setDefaultPermissions(Set.of(defaultPermission));
            request.setRole(Set.of(roleDto));
        }

        entity.setRoles(request.getRole().stream()
                .map(roleMapper::toEntity)
                .collect(Collectors.toSet()));

        entity.setIsActive(true);

        return entity;
    }

    private SignUpResponse buildSignUpResponse(UserEntity entity) {
        SignUpResponse response = new SignUpResponse();
        response.setUserId(entity.getId());
        response.setUsername(entity.getUsername());
        response.setRoles(entity.getRoles().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toSet()));
        response.setPassConfirmationCode(entity.getIsActive());
        return response;
    }
}