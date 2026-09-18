package com.magnet.ecommerce.identity_service.user.controller;

import com.magnet.ecommerce.identity_service.exception.ErrorResponse;
import com.magnet.ecommerce.identity_service.user.dto.CreateUserRequest;
import com.magnet.ecommerce.identity_service.user.dto.UpdateUserRequest;
import com.magnet.ecommerce.identity_service.user.dto.UpdateUserStatusRequest;
import com.magnet.ecommerce.identity_service.user.dto.UserDeleteResponse;
import com.magnet.ecommerce.identity_service.user.dto.UserResponse;
import com.magnet.ecommerce.identity_service.user.dto.UserRolesResponse;
import com.magnet.ecommerce.identity_service.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(
        name = "User Management",
        description = "APIs for creating, viewing, updating, deleting, "
                + "and managing users and their roles"
)
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
    @Operation(
            summary = "Get all users",
            description = "Returns a list of all users. "
                    + "Requires USER_READ permission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
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
    @Operation(
            summary = "Get user by ID",
            description = "Returns a specific user using their UUID. "
                    + "Requires USER_READ permission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
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
    @Operation(
            summary = "Create user",
            description = "Creates a new user account. "
                    + "Requires USER_CREATE permission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already exists",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
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
    @Operation(
            summary = "Update user",
            description = "Updates an existing user's information. "
                    + "Requires USER_UPDATE permission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
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
    @Operation(
            summary = "Delete user",
            description = "Deletes a user account using the user's UUID. "
                    + "Requires USER_DELETE permission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<UserDeleteResponse> deleteUser(
            @PathVariable UUID id) {

        UserDeleteResponse response =
                userService.deleteUser(id);

        return ResponseEntity.ok(response);
    }

    /*
     * UPDATE USER STATUS
     *
     * PATCH /api/v1/users/{id}/status
     *
     * Required permission:
     * USER_UPDATE
     */
    @Operation(
            summary = "Update user status",
            description = "Changes the status of an existing user account. "
                    + "Requires USER_UPDATE permission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status request",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable UUID id,
            @RequestBody UpdateUserStatusRequest request) {

        UserResponse response =
                userService.updateUserStatus(id, request);

        return ResponseEntity.ok(response);
    }

    /*
     * GET USER ROLES
     *
     * GET /api/v1/users/{id}/roles
     *
     * Required permission:
     * USER_READ
     */
    @Operation(
            summary = "Get user roles",
            description = "Returns all roles assigned to a user. "
                    + "Requires USER_READ permission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User roles retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserRolesResponse> getUserRoles(
            @PathVariable UUID id) {

        UserRolesResponse response =
                userService.getUserRoles(id);

        return ResponseEntity.ok(response);
    }

    /*
     * ASSIGN ROLE
     *
     * POST /api/v1/users/{id}/roles/{roleName}
     *
     * Required permission:
     * USER_UPDATE
     */
    @Operation(
            summary = "Assign role to user",
            description = "Assigns a role to an existing user. "
                    + "Requires USER_UPDATE permission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Role assigned successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or role not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping("/{id}/roles/{roleName}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserRolesResponse> assignRole(
            @PathVariable UUID id,
            @PathVariable String roleName) {

        UserRolesResponse response =
                userService.assignRole(id, roleName);

        return ResponseEntity.ok(response);
    }

    /*
     * REMOVE ROLE
     *
     * DELETE /api/v1/users/{id}/roles/{roleName}
     *
     * Required permission:
     * USER_UPDATE
     */
    @Operation(
            summary = "Remove role from user",
            description = "Removes a role from an existing user. "
                    + "Requires USER_UPDATE permission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Role removed successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or role not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
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