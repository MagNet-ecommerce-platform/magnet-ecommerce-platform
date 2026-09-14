package com.magnet.ecommerce.identity_service.role.service;

import com.magnet.ecommerce.identity_service.entity.Permission;
import com.magnet.ecommerce.identity_service.entity.Role;
import com.magnet.ecommerce.identity_service.repository.PermissionRepository;
import com.magnet.ecommerce.identity_service.repository.RoleRepository;
import com.magnet.ecommerce.identity_service.role.dto.RolePermissionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public void assignPermission(
            String roleName,
            String permissionName) {

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found: " + roleName
                        )
                );

        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Permission not found: " + permissionName
                        )
                );

        if (role.getPermissions().contains(permission)) {
            throw new IllegalArgumentException(
                    "Permission already assigned to role"
            );
        }

        role.addPermission(permission);

        roleRepository.save(role);
    }

    @Transactional(readOnly = true)
    public RolePermissionsResponse getPermissions(
            String roleName) {

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found: " + roleName
                        )
                );

        List<String> permissions = role.getPermissions()
                .stream()
                .map(Permission::getName)
                .sorted()
                .toList();

        return new RolePermissionsResponse(
                role.getName(),
                permissions
        );
    }

    public void removePermission(
            String roleName,
            String permissionName) {

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found: " + roleName
                        )
                );

        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Permission not found: " + permissionName
                        )
                );

        if (!role.getPermissions().contains(permission)) {
            throw new IllegalArgumentException(
                    "Permission is not assigned to role"
            );
        }

        role.removePermission(permission);

        roleRepository.save(role);
    }
}