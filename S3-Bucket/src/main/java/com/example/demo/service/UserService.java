package com.example.demo.service;

import com.example.demo.Repository.UserRepository;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String registerUser(User user)  {

        if (userRepository.existsByEmail(user.getEmail())) {
            return "User with email " + user.getEmail() + " already exists.";
        }

        userRepository.save(user);
        return "User registered successfully!";
    }
}

