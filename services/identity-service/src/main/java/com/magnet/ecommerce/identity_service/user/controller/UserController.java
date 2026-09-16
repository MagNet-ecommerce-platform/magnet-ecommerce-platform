package com.magnet.ecommerce.identity_service.user.controller;

import com.magnet.ecommerce.identity_service.user.dto.CreateUserRequest;
import com.magnet.ecommerce.identity_service.user.dto.UpdateUserRequest;
import com.magnet.ecommerce.identity_service.user.dto.UserDeleteResponse;
import com.magnet.ecommerce.identity_service.user.dto.UserResponse;
import com.magnet.ecommerce.identity_service.user.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import com.magnet.ecommerce.identity_service.user.dto.UpdateUserStatusRequest;
import com.magnet.ecommerce.identity_service.user.dto.UserRolesResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /*
     * GET ALL USERS
     *
     * GET /api/v1/users
     *
     * Required permission:
     * USER_READ
     */
    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    /*
     * GET USER BY ID
     *
     * GET /api/v1/users/{id}
     *
     * Required permission:
     * USER_READ
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }

    /*
     * CREATE USER
     *
     * POST /api/v1/users
     *
     * Required permission:
     * USER_CREATE
     */
    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<UserResponse> createUser(
            @RequestBody CreateUserRequest request) {

        UserResponse response =
                userService.createUser(request);

        return ResponseEntity.ok(response);
    }

    /*
     * UPDATE USER
     *
     * PUT /api/v1/users/{id}
     *
     * Required permission:
     * USER_UPDATE
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest request) {

        UserResponse response =
                userService.updateUser(id, request);

        return ResponseEntity.ok(response);
    }

    /*
     * DELETE USER
     *
     * DELETE /api/v1/users/{id}
     *
     * Required permission:
     * USER_DELETE
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<UserDeleteResponse> deleteUser(
            @PathVariable UUID id) {

        UserDeleteResponse response =
                userService.deleteUser(id);

        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{id}/status")
@PreAuthorize("hasAuthority('USER_UPDATE')")
public ResponseEntity<UserResponse> updateUserStatus(
        @PathVariable UUID id,
        @RequestBody UpdateUserStatusRequest request) {

    UserResponse response =
            userService.updateUserStatus(id, request);

    return ResponseEntity.ok(response);
}
@GetMapping("/{id}/roles")
@PreAuthorize("hasAuthority('USER_READ')")
public ResponseEntity<UserRolesResponse> getUserRoles(
        @PathVariable UUID id) {

    UserRolesResponse response =
            userService.getUserRoles(id);

    return ResponseEntity.ok(response);
}
@PostMapping("/{id}/roles/{roleName}")
@PreAuthorize("hasAuthority('USER_UPDATE')")
public ResponseEntity<UserRolesResponse> assignRole(
        @PathVariable UUID id,
        @PathVariable String roleName) {

    UserRolesResponse response =
            userService.assignRole(id, roleName);

    return ResponseEntity.ok(response);
}
@DeleteMapping("/{id}/roles/{roleName}")
@PreAuthorize("hasAuthority('USER_UPDATE')")
public ResponseEntity<UserRolesResponse> removeRole(
        @PathVariable UUID id,
        @PathVariable String roleName) {

    UserRolesResponse response =
            userService.removeRole(id, roleName);

    return ResponseEntity.ok(response);
}
}
