package com.example.springclient.repository;

import com.example.springclient.models.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDTO, Long> {
    UserDTO findByUsername(String username);
}