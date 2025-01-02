package com.lucas.todoapp2.controllers;

import com.lucas.todoapp2.dtos.UserResponseDTO;
import com.lucas.todoapp2.entities.User;
import com.lucas.todoapp2.services.TokenService;
import com.lucas.todoapp2.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    TokenService tokenService;

    /**
     * get user email and todos
     *
     * @param jwt an JWT stored in the cookies
     * @return a response with user details or
     *          unauthorized status
     */
    @GetMapping
    public ResponseEntity<UserResponseDTO> getUser(@CookieValue(name = "JWT", required = false) String jwt) {
        if (jwt == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Long userId = tokenService.getUserIdFromToken(jwt);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Optional<User> user = userService.getUserById(userId);
        if (user.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        UserResponseDTO response = new UserResponseDTO(user.get().getId(), user.get().getEmail(), user.get().getTodos());
        return ResponseEntity.ok(response);
    }

    /**
     * delete user and all associated todos
     *
     * @param jwt stored in cookie
     * @param response response clearing the cookie
     * @return a response with a successful message or
     *          unauthorized status
     */
    @DeleteMapping
    public ResponseEntity<String> deleteUser(@CookieValue(name = "JWT", required = false) String jwt, HttpServletResponse response) {
        if (jwt == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Long userId = tokenService.getUserIdFromToken(jwt);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String msg = userService.delete(userId);
        if (msg == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Cookie cookie = new Cookie("JWT", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok(msg);
    }
}
