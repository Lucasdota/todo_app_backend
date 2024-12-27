package com.lucas.todoapp2.services;

import com.lucas.todoapp2.entities.Todo;
import com.lucas.todoapp2.entities.User;
import com.lucas.todoapp2.repositories.TodoRepository;
import com.lucas.todoapp2.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TodoService {

    @Autowired
    TodoRepository todoRepository;
    @Autowired
    UserRepository userRepository;

    public String create(Long userId, String name, String description) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) return null;
        Todo newTodo = new Todo(name, description);
        newTodo.setUser(user.get());
        todoRepository.save(newTodo);
        return "Todo created successfully";
    }

    public String update(Long todoId) {
        Optional<Todo> todo = todoRepository.findById(todoId);
        if (todo.isEmpty()) return null;
        todo.get().toggleDone();
        todoRepository.save(todo.get());
        return "Todo updated successfully";
    }

    public String delete(Long todoId) {
        Optional<Todo> todo = todoRepository.findById(todoId);
        if (todo.isEmpty()) return null;
        todoRepository.delete(todo.get());
        return "Todo deleted successfully";
    }
}
