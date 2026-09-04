package com.magnet.ecommerce.identity_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.magnet.ecommerce.identity_service.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Override
    Optional<Role> findById(Long id);

    @Override
    List<Role> findAll();

    @Override
    <S extends Role> S save(S role);

    @Override
    void delete(Role role);

    @Override
    void deleteById(Long id);

    @Override
    boolean existsById(Long id);

    @Override
    long count();

    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}