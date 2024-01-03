package com.example.demo.config;

import com.example.demo.management.repository.AdminRepository;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.management.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AdminRepository adminRepository;

    private final StudentRepository studentRepository;

    private final TeacherRepository teacherRepository;

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> {
            UserDetails adminUser = adminRepository.findByUsername(username)
                    .orElse(null);

            if (adminUser != null) {
                return adminUser;
            }

            UserDetails teacherUser = teacherRepository.findByUsername(username)
                    .orElse(null);

            if (teacherUser != null) {
                return teacherUser;
            }

            return studentRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
