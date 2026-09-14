package com.magnet.ecommerce.identity_service.role.controller;

import com.magnet.ecommerce.identity_service.role.dto.PermissionAssignmentResponse;
import com.magnet.ecommerce.identity_service.role.dto.RolePermissionsResponse;
import com.magnet.ecommerce.identity_service.role.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @PostMapping("/{roleName}/permissions/{permissionName}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<PermissionAssignmentResponse> assignPermission(
            @PathVariable String roleName,
            @PathVariable String permissionName) {

        rolePermissionService.assignPermission(
                roleName,
                permissionName
        );

        PermissionAssignmentResponse response =
                new PermissionAssignmentResponse(
                        "Permission assigned successfully",
                        roleName,
                        permissionName
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roleName}/permissions")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<RolePermissionsResponse> getPermissions(
            @PathVariable String roleName) {

        RolePermissionsResponse response =
                rolePermissionService.getPermissions(roleName);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{roleName}/permissions/{permissionName}")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public ResponseEntity<PermissionAssignmentResponse> removePermission(
        @PathVariable String roleName,
        @PathVariable String permissionName) {

    rolePermissionService.removePermission(
            roleName,
            permissionName
    );

    PermissionAssignmentResponse response =
            new PermissionAssignmentResponse(
                    "Permission removed successfully",
                    roleName,
                    permissionName
            );

    return ResponseEntity.ok(response);
}
}