package com.magnet.ecommerce.identity_service.role.controller;

import com.magnet.ecommerce.identity_service.exception.ErrorResponse;
import com.magnet.ecommerce.identity_service.role.dto.PermissionAssignmentResponse;
import com.magnet.ecommerce.identity_service.role.dto.RolePermissionsResponse;
import com.magnet.ecommerce.identity_service.role.service.RolePermissionService;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(
        name = "Role Permission Management",
        description = "APIs for assigning, viewing, and removing permissions "
                + "from roles"
)
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    /*
     * ASSIGN PERMISSION TO ROLE
     *
     * POST /api/v1/roles/{roleName}/permissions/{permissionName}
     *
     * Required role:
     * SUPER_ADMIN or ADMIN
     */
    @Operation(
            summary = "Assign permission to role",
            description = "Assigns a permission to a role. "
                    + "Requires SUPER_ADMIN or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Permission assigned successfully"
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
                    description = "Role or permission not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Permission is already assigned to the role",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
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

    /*
     * GET ROLE PERMISSIONS
     *
     * GET /api/v1/roles/{roleName}/permissions
     *
     * Required role:
     * SUPER_ADMIN or ADMIN
     */
    @Operation(
            summary = "Get role permissions",
            description = "Returns all permissions assigned to a role. "
                    + "Requires SUPER_ADMIN or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Role permissions retrieved successfully"
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
    @GetMapping("/{roleName}/permissions")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<RolePermissionsResponse> getPermissions(
            @PathVariable String roleName) {

        RolePermissionsResponse response =
                rolePermissionService.getPermissions(roleName);

        return ResponseEntity.ok(response);
    }

    /*
     * REMOVE PERMISSION FROM ROLE
     *
     * DELETE /api/v1/roles/{roleName}/permissions/{permissionName}
     *
     * Required role:
     * SUPER_ADMIN or ADMIN
     */
    @Operation(
            summary = "Remove permission from role",
            description = "Removes a permission from a role. "
                    + "Requires SUPER_ADMIN or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Permission removed successfully"
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
                    description = "Role or permission not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
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
