package com.magnet.ecommerce.identity_service.security.jwt;

import com.magnet.ecommerce.identity_service.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        this.signingKey = Keys.hmacShaKeyFor(
                java.util.Base64.getDecoder()
                        .decode(jwtProperties.secret())
        );
    }

    public String generateAccessToken(UserDetails userDetails) {

        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("authorities", authorities)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtProperties.accessTokenExpiration()
                        )
                )
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("tokenType", "REFRESH")
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtProperties.refreshTokenExpiration()
                        )
                )
                .signWith(signingKey)
                .compact();
    }

    public boolean isRefreshToken(String token) {

        try {
            Claims claims = extractAllClaims(token);
    
            return "REFRESH".equals(
                    claims.get("tokenType", String.class)
            );
    
        } catch (io.jsonwebtoken.ExpiredJwtException exception) {
    
            return "REFRESH".equals(
                    exception.getClaims()
                            .get("tokenType", String.class)
            );
    
        } catch (Exception exception) {
    
            return false;
        }
    }

    public String extractUsername(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    public Date extractExpiration(String token) {

        return extractAllClaims(token)
                .getExpiration();
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");
    
            byte[] hash =
                    digest.digest(
                            token.getBytes(StandardCharsets.UTF_8)
                    );
    
            StringBuilder hexString = new StringBuilder();
    
            for (byte b : hash) {
                hexString.append(
                        String.format("%02x", b)
                );
            }
    
            return hexString.toString();
    
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Could not hash refresh token",
                    exception
            );
        }
    }

    public boolean isAccessToken(String token) {

        try {
            Claims claims = extractAllClaims(token);
    
            return !"REFRESH".equals(
                    claims.get("tokenType", String.class)
            );
    
        } catch (io.jsonwebtoken.ExpiredJwtException exception) {
    
            return false;
    
        } catch (Exception exception) {
    
            return false;
        }
    }
}