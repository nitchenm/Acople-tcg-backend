package com.acople.tcg_backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acople.tcg_backend.model.User;
import com.acople.tcg_backend.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> login(String username, String password){
        Optional<User> userOpt = userRepository.findByUsername(username);  

        if(userOpt.isPresent()){
            User user = userOpt.get();
            if(user.getPassword().equals(password)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public User register(User user){
        return userRepository.save(user);
    }
}
