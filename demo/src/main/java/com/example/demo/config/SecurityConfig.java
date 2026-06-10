//package com.example.demo.config;
//
//import com.example.demo.management.authentication.component.JwtAuthenticationEntryPoint;
//import com.example.demo.management.repository.UserPermissionsRepository;
//import com.example.demo.management.security.CustomPermissionEvaluator;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
//import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//@EnableMethodSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
//    private final JwtAuthenticationFilter authenticationFilter;
//    private final AuthenticationProvider authenticationProvider;
//    private final UserDetailsService userDetailsService;
//    private final UserPermissionsRepository userPermissionsRepository;
//
//
/// /    @Bean
/// /    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
/// /        http.cors(Customizer.withDefaults());
/// /        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests((authorize) -> {
/// /            authorize.requestMatchers(HttpMethod.GET,"/actuator/**").permitAll();
/// /            authorize.requestMatchers(HttpMethod.POST, "/api/v1/authentication/sign-in", "/api/v1/authentication/refresh-token").permitAll();
/// /            authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
/// /            authorize.requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll();
/// /            authorize.requestMatchers(HttpMethod.GET, "/api-docs/**").permitAll();
/// /            authorize.anyRequest().authenticated();
/// /        });
/// /
/// /        http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
/// /
/// /        http.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint));
/// /
/// /        http.userDetailsService(userDetailsService);
/// /        http.authenticationProvider(authenticationProvider);
/// /        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
/// /
/// /        return http.build();
/// /    }
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
////    private final AuthenticationProvider authenticationProvider;
//
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(Customizer.withDefaults())
//
//                .authorizeHttpRequests((request)->request
//                        .requestMatchers("/api/v1/authentication/sign-in", "/api/v1/authentication/refresh-token").permitAll()
//                        .anyRequest()
//                        .authenticated())
//
//                .sessionManagement((session)->session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("*"));
//        configuration.setAllowedMethods(Arrays.asList("*"));
//        configuration.setAllowCredentials(true);
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
//    @Bean
//    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
//        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
//        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator(userPermissionsRepository));
//        return expressionHandler;
//    }
//
//}


package com.example.demo.config;

import com.example.demo.management.authentication.component.JwtAuthenticationEntryPoint;
import com.example.demo.management.repository.UserPermissionsRepository;
import com.example.demo.management.security.CustomPermissionEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Component
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Assuming this is the filter you want to use
    private final AuthenticationProvider authenticationProvider;
    private final UserDetailsService userDetailsService; // Added back as it was in the commented code
    private final UserPermissionsRepository userPermissionsRepository;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless APIs
                .cors(Customizer.withDefaults()) // Apply the CORS configuration defined below

                .authorizeHttpRequests((request) -> request
                        // Explicitly permit OPTIONS requests for CORS preflight checks
                        .requestMatchers(HttpMethod.POST, "/**").permitAll()
                        // Permit specific authentication endpoints
                        .requestMatchers("/api/v1/authentication/sign-in", "/api/v1/authentication/refresh-token", "/attachments/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/center/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        ).permitAll()
                        // Require authentication for all other requests
                        .anyRequest()
                        .authenticated())

                .sessionManagement((session) -> session
                        // Use stateless sessions, typical for JWT-based authentication
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider)
                // Add your JWT filter before the standard UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Configure exception handling for unauthorized access
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // ← Allow all origins
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // allow cookies/token headers if needed
        config.setExposedHeaders(List.of("Authorization")); // expose token headers to browser
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // apply to all paths
        return source;
    }


    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator(userPermissionsRepository));
        return expressionHandler;
    }

}
