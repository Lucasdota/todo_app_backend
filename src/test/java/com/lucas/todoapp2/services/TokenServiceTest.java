package com.lucas.todoapp2.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lucas.todoapp2.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.mockito.Mock;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;
    @Mock
    private Algorithm algorithm;
    @Value("${jwt.secret}")
    private final String secret = Base64.getEncoder().encodeToString("mySecretKey123".getBytes());

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenService.secret = secret;
    }

    @Test
    public void testGenerateToken_Success() {
        User mockUser  = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setId(1L);

        String token = tokenService.generateToken(mockUser);

        assertNotNull(token);
        DecodedJWT decodedJWT = JWT.decode(token);
        assertEquals("auth-api", decodedJWT.getIssuer());
        assertEquals(mockUser.getEmail(), decodedJWT.getSubject());
        assertEquals(1L, decodedJWT.getClaim("userId").asLong());
    }

    @Test
    public void testValidateToken_Success() {
        User mockUser  = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setId(1L);

        String token = tokenService.generateToken(mockUser);
        String email = tokenService.validateToken(token);

        assertNotNull(email);
        assertEquals(mockUser.getEmail(), email);
    }

    @Test
    public void testValidateToken_InvalidJWT() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.validateToken("invalidToken");
        });
        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    public void testGetUserIdFromToken_Success() {
        User mockUser  = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setId(1L);

        String token = tokenService.generateToken(mockUser);
        Long userId = tokenService.getUserIdFromToken(token);

        assertNotNull(userId);
        DecodedJWT decodedJWT = JWT.decode(token);
        assertEquals(userId, decodedJWT.getClaim("userId").asLong());
    }

    @Test
    public void testGetUserIdFromToken_InvalidJWT() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.getUserIdFromToken("invalidToken");
        });
        assertEquals("Invalid token", exception.getMessage());
    }
}
