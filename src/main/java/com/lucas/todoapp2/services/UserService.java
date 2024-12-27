package com.lucas.todoapp2.services;

import com.lucas.todoapp2.entities.User;
import com.lucas.todoapp2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void create(String email, String encryptedPassword) {
        User newUser = new User(email, encryptedPassword);
        userRepository.save(newUser);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public UserDetails getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String delete(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) return null;
        userRepository.deleteById(userId);
        return "User deleted successfully";
    }
}
