package com.example.springclient.repository;

import com.example.springclient.models.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends JpaRepository<UserDTO, Long> {
    UserDTO findByUsername(String username);

    @Modifying
    @Query(value = "SELECT * FROM users where company_id= :companyId", nativeQuery = true)
    List<UserDTO> findByCompany_id(Integer companyId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET company_id = null WHERE id = :id", nativeQuery = true)
    void updateCompany_id(Integer id);
}