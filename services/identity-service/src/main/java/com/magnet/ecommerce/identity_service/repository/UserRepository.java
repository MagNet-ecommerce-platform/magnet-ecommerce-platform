package com.magnet.ecommerce.identity_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.magnet.ecommerce.identity_service.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    List<User> findAll();

    <S extends User> S save(S entity);

    boolean existsByEmail(String email);
}