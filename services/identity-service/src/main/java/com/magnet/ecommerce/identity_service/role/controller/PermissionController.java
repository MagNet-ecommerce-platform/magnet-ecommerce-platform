package com.magnet.ecommerce.identity_service.role.controller;

import com.magnet.ecommerce.identity_service.exception.ErrorResponse;
import com.magnet.ecommerce.identity_service.role.dto.CreatePermissionRequest;
import com.magnet.ecommerce.identity_service.role.dto.PermissionDeleteResponse;
import com.magnet.ecommerce.identity_service.role.dto.PermissionResponse;
import com.magnet.ecommerce.identity_service.role.dto.UpdatePermissionRequest;
import com.magnet.ecommerce.identity_service.role.service.PermissionService;

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
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(
        name = "Permission Management",
        description = "APIs for creating, viewing, updating, and deleting permissions"
)
public class PermissionController {

    private final PermissionService permissionService;

    /*
     * GET ALL PERMISSIONS
     *
     * GET /api/v1/permissions
     *
     * Required role:
     * SUPER_ADMIN or ADMIN
     */
    @Operation(
            summary = "Get all permissions",
            description = "Returns all permissions configured in the identity service. "
                    + "Requires SUPER_ADMIN or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Permissions retrieved successfully"
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
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {

        return ResponseEntity.ok(
                permissionService.getAllPermissions()
        );
    }

    /*
     * GET PERMISSION BY NAME
     *
     * GET /api/v1/permissions/{permissionName}
     *
     * Required role:
     * SUPER_ADMIN or ADMIN
     */
    @Operation(
            summary = "Get permission by name",
            description = "Returns a specific permission using its permission name. "
                    + "Requires SUPER_ADMIN or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Permission retrieved successfully"
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
                    description = "Permission not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{permissionName}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<PermissionResponse> getPermissionByName(
            @PathVariable String permissionName) {

        return ResponseEntity.ok(
                permissionService.getPermissionByName(permissionName)
        );
    }

    /*
     * CREATE PERMISSION
     *
     * POST /api/v1/permissions
     *
     * Required role:
     * SUPER_ADMIN or ADMIN
     */
    @Operation(
            summary = "Create permission",
            description = "Creates a new permission with a name and description. "
                    + "Requires SUPER_ADMIN or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Permission created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid permission data",
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
                    description = "Permission already exists",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
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

    /*
     * UPDATE PERMISSION
     *
     * PUT /api/v1/permissions/{permissionName}
     *
     * Required role:
     * SUPER_ADMIN or ADMIN
     */
    @Operation(
            summary = "Update permission",
            description = "Updates the description of an existing permission. "
                    + "Requires SUPER_ADMIN or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Permission updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid permission data",
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
                    description = "Permission not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
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

    /*
     * DELETE PERMISSION
     *
     * DELETE /api/v1/permissions/{permissionName}
     *
     * Required role:
     * SUPER_ADMIN or ADMIN
     */
    @Operation(
            summary = "Delete permission",
            description = "Deletes an existing permission using its permission name. "
                    + "Requires SUPER_ADMIN or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Permission deleted successfully"
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
                    description = "Permission not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
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