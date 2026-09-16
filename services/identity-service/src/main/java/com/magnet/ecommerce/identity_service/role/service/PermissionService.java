package com.magnet.ecommerce.identity_service.role.service;

import com.magnet.ecommerce.identity_service.entity.Permission;
import com.magnet.ecommerce.identity_service.repository.PermissionRepository;
import com.magnet.ecommerce.identity_service.role.dto.PermissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<PermissionResponse> getAllPermissions() {

        return permissionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PermissionResponse getPermissionByName(
            String permissionName) {

        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Permission not found: " + permissionName
                        )
                );

        return toResponse(permission);
    }

    @Transactional
    public PermissionResponse createPermission(
            String name,
            String description) {

        if (permissionRepository.existsByName(name)) {
            throw new IllegalArgumentException(
                    "Permission already exists: " + name
            );
        }

        Instant now = Instant.now();

        Permission permission = new Permission(
                UUID.randomUUID(),
                name,
                description,
                now,
                now
        );

        Permission savedPermission =
                permissionRepository.save(permission);

        return toResponse(savedPermission);
    }

    @Transactional
    public PermissionResponse updatePermission(
            String permissionName,
            String description) {

        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Permission not found: " + permissionName
                        )
                );

        permission.setDescription(description);

        Permission savedPermission =
                permissionRepository.save(permission);

        return toResponse(savedPermission);
    }

    private PermissionResponse toResponse(Permission permission) {

        return new PermissionResponse(
                permission.getId(),
                permission.getName(),
                permission.getDescription()
        );
    }
    @Transactional
    public void deletePermission(String permissionName) {

    Permission permission = permissionRepository.findByName(permissionName)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Permission not found: " + permissionName
                    )
            );

    permissionRepository.delete(permission);
}
}