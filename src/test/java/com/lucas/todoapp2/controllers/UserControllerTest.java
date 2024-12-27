package com.lucas.todoapp2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucas.todoapp2.dtos.UserResponseDTO;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserService userService;
    @Mock
    private UserResponseDTO userResponseDTO;
    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetUser_Success() throws Exception {
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("Password123!");
        mockUser.setId(1L);

        when(tokenService.getUserIdFromToken("valid.jwt.token")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(Optional.of(mockUser));

        UserResponseDTO response = new UserResponseDTO(1L, mockUser.getEmail(), mockUser.getTodos());

        // Convert the response DTO to JSON
        String expectedJson = objectMapper.writeValueAsString(response);

        mockMvc.perform(get("/user")
                        .cookie(new Cookie("JWT", "valid.jwt.token")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Check content type
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void testGetUser_NullJWT() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetUser_NullUserId() throws Exception {
        when(tokenService.getUserIdFromToken("valid.jwt.token")).thenReturn(null);
        mockMvc.perform(get("/user")
                        .cookie(new Cookie("JWT", "valid.jwt.token")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetUser_EmptyUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/user")
                        .cookie(new Cookie("JWT", "valid.jwt.token")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        when(tokenService.getUserIdFromToken("valid.jwt.token")).thenReturn(1L);
        when(userService.delete(1L)).thenReturn("User deleted successfully");

        mockMvc.perform(delete("/user")
                        .cookie(new Cookie("JWT", "valid.jwt.token")))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"))
                .andExpect(cookie().httpOnly("JWT", true))
                .andExpect(cookie().path("JWT", "/"))
                .andExpect(cookie().maxAge("JWT", 0));
    }

    @Test
    public void testDeleteUser_NullJWT() throws Exception {
        mockMvc.perform(delete("/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteUser_NullUserId() throws Exception {
        when(tokenService.getUserIdFromToken("valid.jwt.token")).thenReturn(null);
        mockMvc.perform(delete("/user")
                        .cookie(new Cookie("JWT", "valid.jwt.token")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteUser_EmptyUser() throws Exception {
        when(userService.delete(1L)).thenReturn(null);
        mockMvc.perform(delete("/user")
                        .cookie(new Cookie("JWT", "valid.jwt.token")))
                .andExpect(status().isUnauthorized());
    }
}
