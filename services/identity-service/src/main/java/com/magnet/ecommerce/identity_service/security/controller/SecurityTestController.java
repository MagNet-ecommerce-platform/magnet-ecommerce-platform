package com.magnet.ecommerce.identity_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
public class SecurityTestController {

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