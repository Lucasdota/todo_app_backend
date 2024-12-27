package com.lucas.todoapp2.services;

import com.lucas.todoapp2.entities.User;
import com.lucas.todoapp2.repositories.TodoRepository;
import com.lucas.todoapp2.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreate_Success() {
        String email = "test@example.com";
        String encryptedPassword = "j8d21jdh5dj2198dh219hd912hd189d19821d";

        userService.create(email, encryptedPassword);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testGetUserById_Success() {
        String email = "test@example.com";
        String encryptedPassword = "j8d21jdh5dj2198dh219hd912hd189d19821d";
        User mockUser = new User(email, encryptedPassword);
        mockUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(mockUser , result.get());
    }

    @Test
    public void testGetUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<User> result = userService.getUserById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetUserByEmail_Success() {
        String email = "test@example.com";
        String encryptedPassword = "j8d21jdh5dj2198dh219hd912hd189d19821d";
        User mockUser = new User(email, encryptedPassword);

        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        UserDetails result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(mockUser.getEmail() , result.getUsername());
    }

    @Test
    public void testGetUserByEmail_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);
        UserDetails result = userService.getUserByEmail("test@example.com");
        assertNull(result);
    }

    @Test
    public void testDelete_Success() {
        String email = "test@example.com";
        String encryptedPassword = "j8d21jdh5dj2198dh219hd912hd189d19821d";
        User mockUser = new User(email, encryptedPassword);
        mockUser.setId(1L);

        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        String result = userService.delete(mockUser.getId());
        assertEquals("User deleted successfully", result);
    }

    @Test
    public void testDelete_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        String result = userService.delete(1L);
        assertNull(result);
    }
}
