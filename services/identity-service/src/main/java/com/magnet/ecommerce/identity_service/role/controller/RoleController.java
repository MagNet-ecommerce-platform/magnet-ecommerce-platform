package com.magnet.ecommerce.identity_service.role.controller;

import com.magnet.ecommerce.identity_service.role.dto.RoleResponse;
import com.magnet.ecommerce.identity_service.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.magnet.ecommerce.identity_service.role.dto.CreateRoleRequest;
import com.magnet.ecommerce.identity_service.role.dto.UpdateRoleRequest;
import com.magnet.ecommerce.identity_service.role.dto.RoleDeleteResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {

        return ResponseEntity.ok(
                roleService.getAllRoles()
        );
    }

    @GetMapping("/{roleName}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<RoleResponse> getRoleByName(
        @PathVariable String roleName) {

    return ResponseEntity.ok(
            roleService.getRoleByName(roleName)
    );
} 
@PostMapping
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public ResponseEntity<RoleResponse> createRole(
        @RequestBody CreateRoleRequest request) {

    RoleResponse response = roleService.createRole(
            request.name(),
            request.description()
    );

    return ResponseEntity.ok(response);
}
@PutMapping("/{roleName}")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public ResponseEntity<RoleResponse> updateRole(
        @PathVariable String roleName,
        @RequestBody UpdateRoleRequest request) {

    RoleResponse response = roleService.updateRole(
            roleName,
            request.description()
    );

    return ResponseEntity.ok(response);
}
@DeleteMapping("/{roleName}")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public ResponseEntity<RoleDeleteResponse> deleteRole(
        @PathVariable String roleName) {

    RoleDeleteResponse response =
            roleService.deleteRole(roleName);

    return ResponseEntity.ok(response);
}
}