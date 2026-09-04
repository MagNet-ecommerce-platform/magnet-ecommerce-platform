package com.magnet.ecommerce.identity_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.magnet.ecommerce.identity_service.entity.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Override
    Optional<Permission> findById(Long id);

    @Override
    List<Permission> findAll();

    @Override
    <S extends Permission> S save(S permission);

    @Override
    void delete(Permission permission);

    @Override
    void deleteById(Long id);

    @Override
    boolean existsById(Long id);

    @Override
    long count();

    Optional<Permission> findByName(String name);

    boolean existsByName(String name);
}