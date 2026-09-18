package com.magnet.ecommerce.identity_service.role.controller;

import com.magnet.ecommerce.identity_service.exception.ErrorResponse;
import com.magnet.ecommerce.identity_service.role.dto.CreateRoleRequest;
import com.magnet.ecommerce.identity_service.role.dto.RoleDeleteResponse;
import com.magnet.ecommerce.identity_service.role.dto.RoleResponse;
import com.magnet.ecommerce.identity_service.role.dto.UpdateRoleRequest;
import com.magnet.ecommerce.identity_service.role.service.RoleService;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(
        name = "Role Management",
        description = "APIs for creating, viewing, updating, and deleting roles"
)
public class RoleController {

    private final RoleService roleService;

    /*
     * GET ALL ROLES
     *
     * GET /api/v1/roles
     *
     * Required role:
     * SUPER_ADMIN or ADMIN
     */
    @Operation(
            summary = "Get all roles",
            description = "Returns all roles configured in the identity service. "
                    + "Requires SUPER_ADMIN or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Roles retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have SUPER_ADMIN or ADMIN role",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {

        return ResponseEntity.ok(
                roleService.getAllRoles()
        );
    }

    /*
     * GET ROLE BY NAME
     *
     * GET /api/v1/roles/{roleName}
     *
     * Required role:
     * SUPER_ADMIN or ADMIN
     */
    @Operation(
            summary = "Get role by name",
            description = "Returns a specific role using its role name. "
                    + "Requires SUPER_ADMIN or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Role retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have SUPER_ADMIN or ADMIN role",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Role not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{roleName}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<RoleResponse> getRoleByName(
            @PathVariable String roleName) {

        return ResponseEntity.ok(
                roleService.getRoleByName(roleName)
        );
    }

    /*
     * CREATE ROLE
     *
     * POST /api/v1/roles
     *
     * Required role:
     * SUPER_ADMIN or ADMIN
     */
    @Operation(
            summary = "Create role",
            description = "Creates a new role with a name and description. "
                    + "Requires SUPER_ADMIN or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Role created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid role data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have SUPER_ADMIN or ADMIN role",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Role already exists",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
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

    /*
     * UPDATE ROLE
     *
     * PUT /api/v1/roles/{roleName}
     *
     * Required role:
     * SUPER_ADMIN or ADMIN
     */
    @Operation(
            summary = "Update role",
            description = "Updates the description of an existing role. "
                    + "Requires SUPER_ADMIN or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Role updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid role data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have SUPER_ADMIN or ADMIN role",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Role not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
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

    /*
     * DELETE ROLE
     *
     * DELETE /api/v1/roles/{roleNa*
    */
     }