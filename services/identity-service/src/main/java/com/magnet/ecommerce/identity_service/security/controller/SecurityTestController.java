package com.magnet.ecommerce.identity_service.security;

import com.magnet.ecommerce.identity_service.exception.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@Tag(
        name = "Security Test",
        description = "Endpoints used to verify authentication and JWT authorities"
)
public class SecurityTestController {

    @Operation(
            summary = "Inspect authenticated user",
            description = "Returns information about the currently authenticated "
                    + "user, including username and granted authorities. "
                    + "Requires authentication."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authenticated user information retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication is required",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/security")
    public Map<String, Object> security(Authentication authentication) {

        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .toList();

        return Map.of(
                "authenticated", authentication.isAuthenticated(),
                "username", authentication.getName(),
                "authorities", authorities
        );
    }
}