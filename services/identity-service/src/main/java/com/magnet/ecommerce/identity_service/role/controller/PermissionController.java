package com.magnet.ecommerce.identity_service.role.controller;

import com.magnet.ecommerce.identity_service.role.dto.CreatePermissionRequest;
import com.magnet.ecommerce.identity_service.role.dto.PermissionResponse;
import com.magnet.ecommerce.identity_service.role.dto.UpdatePermissionRequest;
import com.magnet.ecommerce.identity_service.role.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.magnet.ecommerce.identity_service.role.dto.PermissionDeleteResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {

        return ResponseEntity.ok(
                permissionService.getAllPermissions()
        );
    }

    @GetMapping("/{permissionName}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<PermissionResponse> getPermissionByName(
            @PathVariable String permissionName) {

        return ResponseEntity.ok(
                permissionService.getPermissionByName(permissionName)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<PermissionResponse> createPermission(
            @RequestBody CreatePermissionRequest request) {

        PermissionResponse response =
                permissionService.createPermission(
                        request.name(),
                        request.description()
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{permissionName}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<PermissionResponse> updatePermission(
            @PathVariable String permissionName,
            @RequestBody UpdatePermissionRequest request) {

        PermissionResponse response =
                permissionService.updatePermission(
                        permissionName,
                        request.description()
                );

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{permissionName}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<PermissionDeleteResponse> deletePermission(
            @PathVariable String permissionName) {
    
        permissionService.deletePermission(permissionName);
    
        PermissionDeleteResponse response =
                new PermissionDeleteResponse(
                        "Permission deleted successfully",
                        permissionName
                );
    
        return ResponseEntity.ok(response);
    }
}