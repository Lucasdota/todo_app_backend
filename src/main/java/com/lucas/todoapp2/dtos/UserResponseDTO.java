package com.lucas.todoapp2.dtos;

import com.lucas.todoapp2.entities.Todo;

import java.util.List;

public record UserResponseDTO(Long id, String email, List<Todo> todos) {
}
