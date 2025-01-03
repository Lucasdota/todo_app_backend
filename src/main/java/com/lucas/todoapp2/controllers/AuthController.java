package com.lucas.todoapp2.controllers;

import com.lucas.todoapp2.dtos.LoginDTO;
import com.lucas.todoapp2.dtos.RegisterDTO;
import com.lucas.todoapp2.entities.User;
import com.lucas.todoapp2.services.TokenService;
import com.lucas.todoapp2.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    TokenService tokenService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user and generates a JWT token if the credentials are valid.
     *
     * <p>If a valid JWT token is already present in the cookies, the user is logged in
     * without needing to provide credentials again. If the token is invalid, a bad request
     * response is returned. If no token is present, the method attempts to authenticate the
     * user with the provided email and password.</p>
     *
     * @param data the login credentials containing the user's email and password
     * @param jwt the existing JWT token from the cookies, if present
     * @param response the HttpServletResponse used to add the JWT cookie upon successful login
     * @return a ResponseEntity containing a success message if login is successful,
     *         or an error message if the token is invalid or authentication fails
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Validated LoginDTO data, @CookieValue(name = "JWT", required = false) String jwt, HttpServletResponse response) {
        if (jwt != null) {
            if (tokenService.validateToken(jwt) != null) {
                return ResponseEntity.ok("Login successful with existing JWT");
            } else {
                return ResponseEntity.badRequest().body("Invalid token");
            }
        }

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());

        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(806400);
        response.addCookie(cookie);

        response.addHeader("Set-Cookie", String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None",
                cookie.getName(),
                cookie.getValue(),
                cookie.getMaxAge()));

        return ResponseEntity.ok("Login successful");
    }

    /**
     * Registers a new user and generates a JWT token upon successful registration.
     *
     * <p>This method checks if an account with the provided email already exists.
     * If it does, a bad request response is returned. If the email is unique, the
     * user's password is encrypted, and a new user is created and saved to the repository.
     * After successful registration, the user is authenticated, and a JWT token is generated
     * and added to the response cookies.</p>
     *
     * @param data the registration details containing the user's email and password
     * @param response the HttpServletResponse used to add the JWT cookie upon successful registration
     * @return a ResponseEntity containing a success message if the account is created,
     *         or an error message if the email is already in use
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Validated RegisterDTO data, HttpServletResponse response) {
        if (userService.getUserByEmail(data.email()) != null) return ResponseEntity.badRequest().body("An account already exists with email: " +  data.email());

        String encryptedPassword = passwordEncoder.encode(data.password());
        userService.create(data.email(), encryptedPassword);
        var username = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = authenticationManager.authenticate(username);
        var token = tokenService.generateToken((User) auth.getPrincipal());

        Cookie cookie = new Cookie("JWT", token);
        cookie.setMaxAge(806400);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        response.addHeader("Set-Cookie", String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None",
                cookie.getName(),
                cookie.getValue(),
                cookie.getMaxAge()));

        return ResponseEntity.ok("Account created successfully");
    }

    /**
     * Logout a user by clearing the jwt cookie
     *
     * @param response the HttpServletResponse used to add the cleared cookie
     * @return a ResponseEntity with a success message if the logout is successful
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        response.addHeader("Set-Cookie", String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None",
                cookie.getName(),
                cookie.getValue(),
                cookie.getMaxAge()));

        return ResponseEntity.ok("Logout successful");
    }
}
