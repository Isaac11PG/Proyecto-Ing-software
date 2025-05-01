package com.holamundo.HOLASPRING6CV3.repositories;

import com.holamundo.HOLASPRING6CV3.models.SismoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SismoRepository extends JpaRepository<SismoModel, Long> {
    // Métodos personalizados si los necesitas
}