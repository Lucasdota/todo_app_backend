package com.lucas.todoapp2.config;

import com.lucas.todoapp2.repositories.UserRepository;
import com.lucas.todoapp2.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // register it as a bean
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    /**
     * Validates and authenticates JWT, then authenticates the username and password
     *
     * <p>Get the JWT value from cookies; validates it and retrieve the username(email);
     * grab the user from the db with the email and authenticates the user.</p>
     *
     * @param request object
     * @param response object
     * @param filterChain object
     * @throws IOException if an Input/Output error occurs during the processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    public void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws IOException, ServletException {
        String token = recoverToken(request);
        if (token != null) {
            String email = tokenService.validateToken(token);
            if (email != null) {
                UserDetails user = userRepository.findByEmail(email);
                if (user != null) {
                    var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } filterChain.doFilter(request, response);
    }

    public String recoverToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if ("JWT".equals(cookie.getName())) return cookie.getValue();
            }
        } return null;
    }
}