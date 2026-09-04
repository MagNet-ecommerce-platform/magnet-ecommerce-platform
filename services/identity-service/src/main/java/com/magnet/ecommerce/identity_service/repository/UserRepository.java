package com.magnet.ecommerce.identity_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.magnet.ecommerce.identity_service.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    Optional<User> findById(Long id);

    @Override
    List<User> findAll();

    @Override
    <S extends User> S save(S user);

    @Override
    void delete(User user);

    @Override
    void deleteById(Long id);

    @Override
    boolean existsById(Long id);

    @Override
    long count();

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}