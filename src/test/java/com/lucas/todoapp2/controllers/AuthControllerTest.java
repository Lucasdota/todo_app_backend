package com.lucas.todoapp2.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lucas.todoapp2.entities.User;
import com.lucas.todoapp2.services.TokenService;
import com.lucas.todoapp2.services.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testLogin_ValidJWT() throws Exception {
        when(tokenService.validateToken("valid.jwt.token")).thenReturn("test@example.com");
        mockMvc.perform(post("/auth/login")
                        .cookie(new Cookie("JWT", "valid.jwt.token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"Password123!\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful with existing JWT"));
    }

    @Test
    public void testLogin_InvalidJWT() throws Exception {
        when(tokenService.validateToken("invalid.jwt.token")).thenReturn(null);
        mockMvc.perform(post("/auth/login")
                        .cookie(new Cookie("JWT", "invalid.jwt.token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"Password123!\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid token"));
    }

    @Test
    public void testLogin_NullJWT() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenService.generateToken(any(User.class))).thenReturn("new.jwt.token");

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\", \"password\":\"Password123!\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    public void testRegister_ValidEmail() throws Exception {
        when(userService.getUserByEmail("test@example.com")).thenReturn(null);
        when(passwordEncoder.encode("Password123!")).thenReturn("j18ej1298je19");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenService.generateToken(any(User.class))).thenReturn("new.jwt.token");

        mockMvc.perform(post("/auth/register")
                        .cookie(new Cookie("JWT", "new.jwt.token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"Password123!\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account created successfully"));
    }

    @Test
    public void testRegister_AlreadyExistingEmail() throws Exception {
        User mockUser  = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("Password123!");
        when(userService.getUserByEmail("test@example.com")).thenReturn(mockUser);

        mockMvc.perform(post("/auth/register")
                        .cookie(new Cookie("JWT", "new.jwt.token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"Password123!\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("An account already exists with email: " + mockUser.getEmail()));
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful"))
                .andExpect(cookie().httpOnly("JWT", true))
                .andExpect(cookie().path("JWT", "/"))
                .andExpect(cookie().maxAge("JWT", 0));
    }
}