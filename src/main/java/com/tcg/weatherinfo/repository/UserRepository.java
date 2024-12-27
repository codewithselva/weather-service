package com.tcg.weatherinfo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcg.weatherinfo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    @Modifying
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :id")
    void updateUserActiveStatus(@Param("id") Long id, @Param("active") boolean active);


}