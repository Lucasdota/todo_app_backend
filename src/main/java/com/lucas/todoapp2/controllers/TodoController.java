package com.lucas.todoapp2.controllers;

import com.lucas.todoapp2.dtos.CreateTodoDTO;
import com.lucas.todoapp2.dtos.TodoDTO;
import com.lucas.todoapp2.services.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("todo")
public class TodoController {

    @Autowired
    TodoService todoService;

    /**
     * create todo endpoint with user and todo details
     *
     * @param data contains user id, todo name and todo description
     * @return HttpStatus Created if successful or
     *          bad request if user was not found to assign todo to it
     */
    @PostMapping
    public ResponseEntity<String> createTodo(@RequestBody @Validated CreateTodoDTO data) {
        String todo = todoService.create(data.userId(), data.name(), data.description());
        if (todo == null) return ResponseEntity.badRequest().body("User not found");
        return ResponseEntity.status(HttpStatus.CREATED).body(todo);
    }

    /**
     * toggle the attribute done for the specific todo
     *
     * @param data contains the todo id
     * @return HttpsStatus Accepted if successful or
     *          bad request if todo was not found by given id
     */
    @PutMapping
    public ResponseEntity<String> toggleDone(@RequestBody @Validated TodoDTO data) {
        String todo = todoService.update(data.todoId());
        if (todo == null) return ResponseEntity.badRequest().body("Todo not found");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(todo);
    }

    /**
     * delete todo by given id
     *
     * @param data contains the todo id
     * @return HttpsStatus Accepted if successful or
     *          bad request if todo was not found by given id
     */
    @DeleteMapping
    public ResponseEntity<String> deleteTodo(@RequestBody @Validated TodoDTO data) {
        String todo = todoService.delete(data.todoId());
        if (todo == null) return ResponseEntity.badRequest().body("Todo not found");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(todo);
    }
}
