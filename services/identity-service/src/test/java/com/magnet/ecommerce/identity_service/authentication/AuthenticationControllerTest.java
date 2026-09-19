package com.magnet.ecommerce.identity_service.authentication;

import com.magnet.ecommerce.identity_service.authentication.controller.AuthenticationController;
import com.magnet.ecommerce.identity_service.authentication.dto.AuthenticationResponse;
import com.magnet.ecommerce.identity_service.authentication.dto.LoginRequest;
import com.magnet.ecommerce.identity_service.authentication.dto.LoginResponse;
import com.magnet.ecommerce.identity_service.authentication.dto.RegisterRequest;
import com.magnet.ecommerce.identity_service.authentication.service.AuthenticationService;
import com.magnet.ecommerce.identity_service.config.SecurityConfig;
import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;
import com.magnet.ecommerce.identity_service.passwordreset.dto.ForgotPasswordResponse;
import com.magnet.ecommerce.identity_service.passwordreset.dto.ResetPasswordRequest;
import com.magnet.ecommerce.identity_service.passwordreset.service.PasswordResetService;
import com.magnet.ecommerce.identity_service.security.CustomUserDetailsService;
import com.magnet.ecommerce.identity_service.security.RestAuthenticationEntryPoint;
import com.magnet.ecommerce.identity_service.security.jwt.JwtService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthenticationController.class)
@Import(SecurityConfig.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;


    // ============================================================
    // REGISTER
    // ============================================================

    @Test
    void registerShouldReturn201() throws Exception {

        UUID userId = UUID.randomUUID();

        AuthenticationResponse response =
                new AuthenticationResponse(
                        userId,
                        "testuser",
                        "testuser@example.com",
                        "Test",
                        "User",
                        UserStatus.PENDING
                );

        when(authenticationService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        String requestBody = """
                {
                    "username": "testuser",
                    "email": "testuser@example.com",
                    "password": "Password123!",
                    "firstName": "Test",
                    "lastName": "User"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }


    @Test
    void registerWithInvalidEmailShouldReturn400() throws Exception {

        String requestBody = """
                {
                    "username": "testuser",
                    "email": "invalid-email",
                    "password": "Password123!",
                    "firstName": "Test",
                    "lastName": "User"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void registerWithMissingPasswordShouldReturn400() throws Exception {

        String requestBody = """
                {
                    "username": "testuser",
                    "email": "testuser@example.com",
                    "firstName": "Test",
                    "lastName": "User"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }


    // ============================================================
    // LOGIN
    // ============================================================

    @Test
    void loginShouldReturn200() throws Exception {

        LoginResponse response =
                new LoginResponse(
                        "access-token",
                        "refresh-token",
                        "Bearer",
                        900
                );

        when(authenticationService.login(any(LoginRequest.class)))
                .thenReturn(response);

        String requestBody = """
                {
                    "email": "testuser@example.com",
                    "password": "Password123!"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900));
    }


    @Test
    void loginWithInvalidEmailShouldReturn400() throws Exception {

        String requestBody = """
                {
                    "email": "invalid-email",
                    "password": "Password123!"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void loginWithMissingPasswordShouldReturn400() throws Exception {

        String requestBody = """
                {
                    "email": "testuser@example.com"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }


    // ============================================================
    // REFRESH TOKEN
    // ============================================================

    @Test
    void refreshTokenShouldReturn200() throws Exception {

        LoginResponse response =
                new LoginResponse(
                        "new-access-token",
                        "new-refresh-token",
                        "Bearer",
                        900
                );

        when(authenticationService.refreshAccessToken(anyString()))
                .thenReturn(response);

        String requestBody = """
                {
                    "refreshToken": "old-refresh-token"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/refresh")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900));
    }


    @Test
    void refreshWithMissingTokenShouldReturn400() throws Exception {

        String requestBody = """
                {
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/refresh")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }


    // ============================================================
    // LOGOUT
    // ============================================================

    @Test
    void logoutShouldReturn204() throws Exception {

        doNothing()
                .when(authenticationService)
                .logout(anyString());

        String requestBody = """
                {
                    "refreshToken": "refresh-token"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/logout")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNoContent());
    }


    @Test
    void logoutWithMissingTokenShouldReturn400() throws Exception {

        String requestBody = """
                {
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/logout")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }


    // ============================================================
    // FORGOT PASSWORD
    // ============================================================

    @Test
    void forgotPasswordShouldReturn200() throws Exception {

        ForgotPasswordResponse response =
                new ForgotPasswordResponse(
                        "If the email exists, a password reset request has been created.",
                        "reset-token"
                );

        when(passwordResetService.forgotPassword(anyString()))
                .thenReturn(response);

        String requestBody = """
                {
                    "email": "testuser@example.com"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/forgot-password")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "If the email exists, a password reset request has been created."
                                )
                )
                .andExpect(
                        jsonPath("$.resetToken")
                                .value("reset-token")
                );
    }


    // ============================================================
    // RESET PASSWORD
    // ============================================================

    @Test
    void resetPasswordShouldReturn200() throws Exception {

        doNothing()
                .when(passwordResetService)
                .resetPassword(any(ResetPasswordRequest.class));

        String requestBody = """
                {
                    "token": "reset-token",
                    "newPassword": "NewPassword123!"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/reset-password")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value("Password reset successfully")
                );
    }
}