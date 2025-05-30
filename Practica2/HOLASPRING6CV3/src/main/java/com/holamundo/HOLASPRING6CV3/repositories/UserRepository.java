package com.holamundo.HOLASPRING6CV3.repositories;

import com.holamundo.HOLASPRING6CV3.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByUsername(String username);
}

