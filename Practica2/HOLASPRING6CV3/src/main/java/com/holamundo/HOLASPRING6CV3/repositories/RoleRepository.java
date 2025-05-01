package com.holamundo.HOLASPRING6CV3.repositories;

import com.holamundo.HOLASPRING6CV3.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
