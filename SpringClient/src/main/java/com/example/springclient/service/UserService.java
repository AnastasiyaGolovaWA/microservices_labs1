package com.example.springclient.service;

import com.example.springclient.models.UserDTO;
import com.example.springclient.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<UserDTO> getAll() {
        return userRepository.findAll();
    }

    public Optional<UserDTO> getById(long id) {
        return userRepository.findById(id);
    }

    public UserDTO save(UserDTO newUser) {
        return userRepository.save(newUser);
    }

    public void delete(long id) {
        userRepository.deleteById(id);
    }

    public UserDTO findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}