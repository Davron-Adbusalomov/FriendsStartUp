package com.example.demo.management.authentication.service;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.exception.InvalidLoginRequestException;
import com.example.demo.exception.InvalidRefreshTokenException;
import com.example.demo.management.authentication.enums.RolesEnum;
import com.example.demo.management.dto.AssignUserToGroupDTO;
import com.example.demo.management.dto.request.RefreshTokenRequest;
import com.example.demo.management.dto.request.SaveUserPermissionsDto;
import com.example.demo.management.dto.request.SignInReqDto;
import com.example.demo.management.dto.request.SignUpRequest;
import com.example.demo.management.dto.response.AccessTokenResDto;
import com.example.demo.management.dto.response.SignUpResponse;
import com.example.demo.management.dto.response.UserLoginResponseDto;
import com.example.demo.management.mapper.RoleMapper;
import com.example.demo.management.model.*;
import com.example.demo.management.model.rbac.DefaultPermissionEntity;
import com.example.demo.management.model.rbac.RoleEntity;
import com.example.demo.management.repository.*;
import com.example.demo.management.service.GroupService;
import com.example.demo.management.service.UserPermissionsService;
import com.example.demo.utils.PasswordUtil;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
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
    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final UserPermissionsService userPermissionsService;
    private final GroupService groupService;

    public AccessTokenResDto signInAndGenerateTokens(SignInReqDto signInDto, HttpServletRequest request) {
        Authentication authentication = authenticateUser(signInDto, request);

        UserEntity userEntity;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserEntity) {
            userEntity = (UserEntity) principal;
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            userEntity = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found by username after authentication"));
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
        }

        if (userEntity == null || userEntity.getRoles().stream()
                .noneMatch(role -> role.getName() == RolesEnum.valueOf(signInDto.getRole()))) {
            throw new InvalidLoginRequestException("Invalid login request");
        }

        AccessTokenResDto response = generateTokens(authentication);

        UserLoginResponseDto userDetailsDto = new UserLoginResponseDto();
        userDetailsDto.setId(userEntity.getId());
        userDetailsDto.setUsername(userEntity.getUsername());
        userDetailsDto.setFullName(userEntity.getFullName());
        if (userEntity.getRoles()!=null && !userEntity.getRoles().isEmpty()){
            userDetailsDto.setRoles(userEntity.getRoles().stream().map(e -> e.getName().name()).collect(Collectors.toSet()));
        }
        if (signInDto.getRole()!=null && !signInDto.getRole().isEmpty()){
            if (RolesEnum.STUDENT.name().equals(signInDto.getRole())){
                Optional<Student> student = studentRepository.findById(userEntity.getId());
                student.ifPresent(value -> userDetailsDto.setGroups(value.getGroupings().stream().map(Grouping::getName).toList()));
            }
            else if (RolesEnum.TEACHER.name().equals(signInDto.getRole())){
                Optional<Teacher> teacher = teacherRepository.findById(userEntity.getId());
                teacher.ifPresent(value -> userDetailsDto.setGroups(value.getGroupList().stream().map(Grouping::getName).toList()));
            }
        }

        response.setUserDetails(userDetailsDto);
        return response;
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

        if (entity.getRoles() != null && !entity.getRoles().isEmpty()) {
            SaveUserPermissionsDto userPermissionsDto = new SaveUserPermissionsDto();

            Set<DefaultPermissionEntity> allPermissions = entity.getRoles().stream()
                    .filter(Objects::nonNull)
                    .flatMap(role -> Optional.ofNullable(role.getDefaultPermissions())
                            .orElse(Set.of()).stream())
                    .collect(Collectors.toSet());

            userPermissionsDto.setUserId(entity.getId());
            userPermissionsDto.setPermissions(
                    allPermissions.stream()
                            .map(p -> p.getName().name())
                            .toList()
            );

            userPermissionsService.save(userPermissionsDto);
        }

        if (entity.getRoles() != null && entity.getRoles().stream().anyMatch(e -> e.getName().equals(RolesEnum.ADMIN))) {
            if (adminRepository.findById(entity.getId()).isEmpty()){
                Admin admin = new Admin();
                admin.setId(entity.getId());
                admin.setFullName(entity.getFullName());
                adminRepository.save(admin);
            }
        }

        if (entity.getRoles() != null && entity.getRoles().stream().anyMatch(e -> e.getName().equals(RolesEnum.STUDENT))) {
            if (studentRepository.findById(entity.getId()).isEmpty()){
                Student student = new Student();
                student.setId(entity.getId());
                student.setName(entity.getFullName());
                Student student1 = studentRepository.save(student);

                assignGroup(request, student1.getId(), false);
            }
        }

        if (entity.getRoles() != null && entity.getRoles().stream().anyMatch(e -> e.getName().equals(RolesEnum.TEACHER))) {
            if (teacherRepository.findById(entity.getId()).isEmpty()){
                Teacher teacher = new Teacher();
                teacher.setId(entity.getId());
                teacher.setName(entity.getFullName());
                Teacher teacher1 = teacherRepository.save(teacher);

                assignGroup(request, teacher1.getId(), true);
            }
        }
        return buildSignUpResponse(entity);
    }

    private void assignGroup(SignUpRequest request, Long id, Boolean isTeacher) {
        if (request.getGroups() != null && !request.getGroups().isEmpty()) {
            for (String group : request.getGroups()) {
                AssignUserToGroupDTO dto = new AssignUserToGroupDTO();
                dto.setId(id);
                dto.setGroupName(group);
                if (isTeacher) groupService.assignTeacherToGroup(dto);
                else groupService.assignStudentToGroup(dto);
            }
        }
    }

    private void validateUniqueUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new DataIntegrityViolationException("This username already exists");
        }
    }

    public UserEntity createUserEntity(SignUpRequest request) {
        UserEntity entity = new UserEntity();
        entity.setUsername(request.getUsername());

        long id = ProjectUtils.getCurrentUserDetails().getId();
        entity.setCreatedBy(id);
        entity.setUpdatedBy(id);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setFullName(request.getFullName());

        String rawPassword = PasswordUtil.generatePassword(8);
        entity.setPassword(passwordEncoder.encode("password"));

        Set<RoleEntity> roles = new HashSet<>();

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            RoleEntity defaultRole = roleRepository.findByName(RolesEnum.ROLE_USER);
            roles.add(defaultRole);
        } else {
            for (String roleName : request.getRoles()) {
                RoleEntity role = roleRepository.findByName(RolesEnum.valueOf(roleName));
                roles.add(role);
            }
        }

        entity.setRoles(roles);
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