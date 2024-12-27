package com.lucas.todoapp2.controllers;

import com.lucas.todoapp2.entities.User;
import com.lucas.todoapp2.services.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private TodoService todoService;
    @InjectMocks
    private TodoController todoController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build();
    }

    @Test
    public void testCreateTodo_Success() throws Exception {
        when(todoService.create(1L, "task 1", "do the dishes")).thenReturn("Todo created successfully");
        mockMvc.perform(post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"name\":\"task 1\", \"description\":\"do the dishes\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Todo created successfully"));
    }

    @Test
    public void testCreateTodo_UserNotFound() throws Exception {
        when(todoService.create(1L, "task 1", "do the dishes")).thenReturn(null);
        mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":1,\"name\":\"task 1\", \"description\":\"do the dishes\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    public void testToggleDone_Success() throws Exception {
        when(todoService.update(1L)).thenReturn("Todo updated successfully");
        mockMvc.perform(put("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"todoId\":1}"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Todo updated successfully"));
    }

    @Test
    public void testToggleDone_TodoNotFound() throws Exception {
        when(todoService.update(1L)).thenReturn(null);
        mockMvc.perform(put("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"todoId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Todo not found"));
    }

    @Test
    public void testDeleteTodo_Success() throws Exception {
        when(todoService.delete(1L)).thenReturn("Todo deleted successfully");
        mockMvc.perform(delete("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"todoId\":1}"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Todo deleted successfully"));
    }

    @Test
    public void testDeleteTodo_TodoNotFound() throws Exception {
        when(todoService.delete(1L)).thenReturn(null);
        mockMvc.perform(delete("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"todoId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Todo not found"));
    }
}
