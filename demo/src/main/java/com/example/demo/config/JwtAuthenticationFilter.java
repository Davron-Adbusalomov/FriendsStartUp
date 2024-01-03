package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if(jwtService.isTokenValid(jwt, userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                String authorizationResult = allowUserToEndpoint(userDetails, request);
                if (!"allow".equals(authorizationResult)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, authorizationResult);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String allowUserToEndpoint(UserDetails userData, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        String requestMethodType = request.getMethod();
        List<String> roles = userData.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (roles.isEmpty()) {
            return "User does not have any role";
        }

        if (endpoint.contains("/api")){
            if (roles.contains("ADMIN")){
                return "allow";
            }
        }

        if (endpoint.contains("/api/teacher")){
            if (roles.contains("TEACHER")){
                return "allow";
            }
        }

        if (endpoint.contains("/api/student")){
            if (roles.contains("STUDENT")){
                return "allow";
            }
        }

        if (endpoint.contains("api/question")){
            if (roles.contains("TEACHER")){
                return "allow";
            }
        }

        if (endpoint.contains("api/quiz/createQuiz")){
            if (roles.contains("TEACHER")){
                return "allow";
            }
        }

        if (endpoint.contains("api/quiz/beginQuiz")){
            if (roles.contains("STUDENT")){
                return "allow";
            }
        }

        if (endpoint.contains("api/quiz/checkMultipleChoice")){
            if (roles.contains("STUDENT") || roles.contains("TEACHER")){
                return "allow";
            }
        }

        if (endpoint.contains("api/quiz_results/recordingResult")){
            if (roles.contains("TEACHER")){
                return "allow";
            }
        }

        return "Restricted!";
    }
}
