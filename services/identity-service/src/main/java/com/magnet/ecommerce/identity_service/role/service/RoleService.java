package com.magnet.ecommerce.identity_service.role.service;

import com.magnet.ecommerce.identity_service.entity.Role;
import com.magnet.ecommerce.identity_service.repository.RoleRepository;
import com.magnet.ecommerce.identity_service.role.dto.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.magnet.ecommerce.identity_service.role.dto.RoleDeleteResponse;

import java.util.List;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;

    public List<RoleResponse> getAllRoles() {

        return roleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private RoleResponse toResponse(Role role) {

        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription()
        );
    }

    public RoleResponse getRoleByName(String roleName) {

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found: " + roleName
                        )
                );
    
        return toResponse(role);
    }
    @Transactional
    public RoleResponse createRole(
        String name,
        String description) {

    if (roleRepository.existsByName(name)) {
        throw new IllegalArgumentException(
                "Role already exists: " + name
        );
    }

    Instant now = Instant.now();

    Role role = new Role(
            UUID.randomUUID(),
            name,
            description,
            now,
            now
    );

    Role savedRole = roleRepository.save(role);

    return toResponse(savedRole);
}

@Transactional
public RoleResponse updateRole(
        String roleName,
        String description) {

    Role role = roleRepository.findByName(roleName)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Role not found: " + roleName
                    )
            );

    role.setDescription(description);
    role.setUpdatedAt(Instant.now());

    Role savedRole = roleRepository.save(role);

    return toResponse(savedRole);
}

@Transactional
public RoleDeleteResponse deleteRole(String roleName) {

    Role role = roleRepository.findByName(roleName)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Role not found: " + roleName
                    )
            );

    if (!role.getPermissions().isEmpty()) {
        throw new IllegalArgumentException(
                "Cannot delete role with assigned permissions: " + roleName
        );
    }

    roleRepository.delete(role);

    return new RoleDeleteResponse(
            "Role deleted successfully",
            roleName
    );
}
}