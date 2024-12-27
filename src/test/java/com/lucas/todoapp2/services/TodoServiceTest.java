package com.lucas.todoapp2.services;

import com.lucas.todoapp2.entities.Todo;
import com.lucas.todoapp2.entities.User;
import com.lucas.todoapp2.repositories.TodoRepository;
import com.lucas.todoapp2.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class TodoServiceTest {

    @InjectMocks
    private TodoService todoService;
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
        Long userId = 1L;
        String name = "Task 1";
        String description = "Do the dishes";

        User mockUser  = new User();
        mockUser.setId(userId);
        mockUser.setEmail("test@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        String result = todoService.create(userId, name, description);

        verify(todoRepository, times(1)).save(any(Todo.class));
        assertEquals("Todo created successfully", result);
    }

    @Test
    public void testCreate_UserNotFound() {
        Long userId = 1L;
        String name = "Task 1";
        String description = "Do the dishes";

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        String result = todoService.create(userId, name, description);

        verify(todoRepository, never()).save(any(Todo.class));
        assertNull(result);
    }
}
