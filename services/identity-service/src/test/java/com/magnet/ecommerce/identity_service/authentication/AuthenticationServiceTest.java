package com.magnet.ecommerce.identity_service.authentication;

import com.magnet.ecommerce.identity_service.activation.service.ActivationTokenService;
import com.magnet.ecommerce.identity_service.authentication.dto.AuthenticationResponse;
import com.magnet.ecommerce.identity_service.authentication.dto.LoginRequest;
import com.magnet.ecommerce.identity_service.authentication.dto.LoginResponse;
import com.magnet.ecommerce.identity_service.authentication.dto.RegisterRequest;
import com.magnet.ecommerce.identity_service.authentication.service.AuthenticationService;
import com.magnet.ecommerce.identity_service.entity.Role;
import com.magnet.ecommerce.identity_service.entity.User;
import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;
import com.magnet.ecommerce.identity_service.exception.AccountLockedException;
import com.magnet.ecommerce.identity_service.exception.InvalidLoginException;
import com.magnet.ecommerce.identity_service.exception.InvalidRefreshTokenException;
import com.magnet.ecommerce.identity_service.kafka.IdentityEventPublisher;
import com.magnet.ecommerce.identity_service.repository.RoleRepository;
import com.magnet.ecommerce.identity_service.repository.UserRepository;
import com.magnet.ecommerce.identity_service.security.CustomUserDetails;
import com.magnet.ecommerce.identity_service.security.CustomUserDetailsService;
import com.magnet.ecommerce.identity_service.security.jwt.JwtService;
import com.magnet.ecommerce.identity_service.token.entity.RefreshToken;
import com.magnet.ecommerce.identity_service.token.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    // ============================================================
    // REGISTER TESTS
    // ============================================================

    @Test
    void shouldRegisterNewUserSuccessfully() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        RegisterRequest request = new RegisterRequest(
                "testuser",
                "testuser@example.com",
                "Password123!",
                "Test",
                "User"
        );

        Role customerRole = mock(Role.class);

        UUID userId = UUID.randomUUID();

        User savedUser = new User(
                userId,
                "testuser",
                "testuser@example.com",
                "encoded-password",
                "Test",
                "User",
                UserStatus.PENDING,
                false,
                Instant.now(),
                Instant.now()
        );

        when(userRepository.existsByEmail("testuser@example.com"))
                .thenReturn(false);

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(false);

        when(passwordEncoder.encode("Password123!"))
                .thenReturn("encoded-password");

        when(roleRepository.findByName("CUSTOMER"))
                .thenReturn(Optional.of(customerRole));

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        AuthenticationResponse response =
                authenticationService.register(request);

        assertNotNull(response);
        assertEquals(userId, response.id());
        assertEquals("testuser", response.username());
        assertEquals("testuser@example.com", response.email());
        assertEquals("Test", response.firstName());
        assertEquals("User", response.lastName());
        assertEquals(UserStatus.PENDING, response.status());

        verify(userRepository)
                .existsByEmail("testuser@example.com");

        verify(userRepository)
                .existsByUsername("testuser");

        verify(passwordEncoder)
                .encode("Password123!");

        verify(roleRepository)
                .findByName("CUSTOMER");

        verify(userRepository)
                .save(any(User.class));

        verify(activationTokenService)
                .createToken(savedUser);

        verify(identityEventPublisher)
                .publishUserRegistered(savedUser);
    }

    @Test
    void shouldRejectRegistrationWhenEmailAlreadyExists() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        RegisterRequest request = new RegisterRequest(
                "testuser",
                "testuser@example.com",
                "Password123!",
                "Test",
                "User"
        );

        when(userRepository.existsByEmail("testuser@example.com"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authenticationService.register(request)
                );

        assertEquals(
                "Email is already registered",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByEmail("testuser@example.com");

        verify(userRepository, never())
                .existsByUsername(anyString());

        verifyNoInteractions(
                passwordEncoder,
                activationTokenService,
                roleRepository,
                identityEventPublisher
        );
    }

    @Test
    void shouldRejectRegistrationWhenUsernameAlreadyExists() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        RegisterRequest request = new RegisterRequest(
                "testuser",
                "testuser@example.com",
                "Password123!",
                "Test",
                "User"
        );

        when(userRepository.existsByEmail("testuser@example.com"))
                .thenReturn(false);

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authenticationService.register(request)
                );

        assertEquals(
                "Username is already registered",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByEmail("testuser@example.com");

        verify(userRepository)
                .existsByUsername("testuser");

        verifyNoInteractions(
                passwordEncoder,
                activationTokenService,
                roleRepository,
                identityEventPublisher
        );
    }

    @Test
    void shouldRejectRegistrationWhenCustomerRoleIsNotConfigured() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        RegisterRequest request = new RegisterRequest(
                "testuser",
                "testuser@example.com",
                "Password123!",
                "Test",
                "User"
        );

        when(userRepository.existsByEmail("testuser@example.com"))
                .thenReturn(false);

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(false);

        when(passwordEncoder.encode("Password123!"))
                .thenReturn("encoded-password");

        when(roleRepository.findByName("CUSTOMER"))
                .thenReturn(Optional.empty());

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> authenticationService.register(request)
                );

        assertEquals(
                "Default CUSTOMER role is not configured",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByEmail("testuser@example.com");

        verify(userRepository)
                .existsByUsername("testuser");

        verify(passwordEncoder)
                .encode("Password123!");

        verify(roleRepository)
                .findByName("CUSTOMER");

        verify(userRepository, never())
                .save(any(User.class));

        verifyNoInteractions(
                activationTokenService,
                identityEventPublisher
        );
    }

    // ============================================================
    // LOGIN TESTS
    // ============================================================

    @Test
    void shouldLoginSuccessfully() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        User user = new User(
                UUID.randomUUID(),
                "testuser",
                "testuser@example.com",
                "encoded-password",
                "Test",
                "User",
                UserStatus.ACTIVE,
                true,
                Instant.now(),
                Instant.now()
        );

        CustomUserDetails userDetails =
                new CustomUserDetails(user);

        Authentication authentication =
                mock(Authentication.class);

        when(userRepository.findByEmail("testuser@example.com"))
                .thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.generateAccessToken(userDetails))
                .thenReturn("access-token");

        when(jwtService.generateRefreshToken(userDetails))
                .thenReturn("refresh-token");

        when(jwtService.hashToken("refresh-token"))
                .thenReturn("refresh-token-hash");

        Instant refreshExpiration =
                Instant.now().plusSeconds(604800);

        when(jwtService.extractExpiration("refresh-token"))
                .thenReturn(Date.from(refreshExpiration));

        LoginResponse response =
                authenticationService.login(
                        new LoginRequest(
                                "testuser@example.com",
                                "Password123!"
                        )
                );

        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(900, response.expiresIn());

        verify(userRepository)
                .findByEmail("testuser@example.com");

        verify(authenticationManager)
                .authenticate(any(
                        UsernamePasswordAuthenticationToken.class
                ));

        verify(jwtService)
                .generateAccessToken(userDetails);

        verify(jwtService)
                .generateRefreshToken(userDetails);

        verify(jwtService)
                .hashToken("refresh-token");

        verify(jwtService)
                .extractExpiration("refresh-token");

        verify(refreshTokenRepository)
                .save(any(RefreshToken.class));
    }

    @Test
    void shouldRejectLoginWhenUserDoesNotExist() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.login(
                        new LoginRequest(
                                "missing@example.com",
                                "Password123!"
                        )
                )
        );

        verify(userRepository)
                .findByEmail("missing@example.com");

        verifyNoInteractions(
                authenticationManager,
                jwtService,
                refreshTokenRepository
        );
    }

    @Test
    void shouldRejectLoginWhenAccountIsLocked() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        User user = new User(
                UUID.randomUUID(),
                "lockeduser",
                "locked@example.com",
                "encoded-password",
                "Locked",
                "User",
                UserStatus.LOCKED,
                false,
                Instant.now(),
                Instant.now()
        );

        when(userRepository.findByEmail("locked@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                AccountLockedException.class,
                () -> authenticationService.login(
                        new LoginRequest(
                                "locked@example.com",
                                "Password123!"
                        )
                )
        );

        verify(userRepository)
                .findByEmail("locked@example.com");

        verifyNoInteractions(
                authenticationManager,
                jwtService,
                refreshTokenRepository
        );
    }

    @Test
    void shouldRejectLoginWithInvalidPassword() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        User user = new User(
                UUID.randomUUID(),
                "testuser",
                "testuser@example.com",
                "encoded-password",
                "Test",
                "User",
                UserStatus.ACTIVE,
                true,
                Instant.now(),
                Instant.now()
        );

        when(userRepository.findByEmail("testuser@example.com"))
                .thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenThrow(new BadCredentialsException("Bad credentials"));

        InvalidLoginException exception =
                assertThrows(
                        InvalidLoginException.class,
                        () -> authenticationService.login(
                                new LoginRequest(
                                        "testuser@example.com",
                                        "WrongPassword123!"
                                )
                        )
                );

        assertEquals(1, exception.getFailedAttempts());
        assertEquals(4, exception.getAttemptsRemaining());

        assertEquals(
                1,
                user.getFailedLoginAttempts()
        );

        verify(userRepository)
                .save(user);

        verifyNoInteractions(
                jwtService,
                refreshTokenRepository
        );
    }

    @Test
    void shouldLockAccountAfterFifthFailedLoginAttempt() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        User user = new User(
                UUID.randomUUID(),
                "testuser",
                "testuser@example.com",
                "encoded-password",
                "Test",
                "User",
                UserStatus.ACTIVE,
                true,
                Instant.now(),
                Instant.now()
        );

        user.setFailedLoginAttempts(4);

        when(userRepository.findByEmail("testuser@example.com"))
                .thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenThrow(new BadCredentialsException("Bad credentials"));

        AccountLockedException exception =
                assertThrows(
                        AccountLockedException.class,
                        () -> authenticationService.login(
                                new LoginRequest(
                                        "testuser@example.com",
                                        "WrongPassword123!"
                                )
                        )
                );

        assertEquals(
                "Account locked. Too many failed login attempts.",
                exception.getMessage()
        );

        assertEquals(
                UserStatus.LOCKED,
                user.getStatus()
        );

        assertFalse(user.isEnabled());

        assertEquals(
                5,
                user.getFailedLoginAttempts()
        );

        assertNotNull(user.getLockedAt());

        verify(userRepository)
                .save(user);

        verifyNoInteractions(
                jwtService,
                refreshTokenRepository
        );
    }

    @Test
    void shouldGenerateAndStoreRefreshTokenAfterSuccessfulLogin() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        User user = new User(
                UUID.randomUUID(),
                "testuser",
                "testuser@example.com",
                "encoded-password",
                "Test",
                "User",
                UserStatus.ACTIVE,
                true,
                Instant.now(),
                Instant.now()
        );

        CustomUserDetails userDetails =
                new CustomUserDetails(user);

        Authentication authentication =
                mock(Authentication.class);

        when(userRepository.findByEmail("testuser@example.com"))
                .thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.generateAccessToken(userDetails))
                .thenReturn("access-token");

        when(jwtService.generateRefreshToken(userDetails))
                .thenReturn("refresh-token");

        when(jwtService.hashToken("refresh-token"))
                .thenReturn("hashed-refresh-token");

        Instant expiresAt =
                Instant.now().plusSeconds(604800);

        when(jwtService.extractExpiration("refresh-token"))
                .thenReturn(Date.from(expiresAt));

        authenticationService.login(
                new LoginRequest(
                        "testuser@example.com",
                        "Password123!"
                )
        );

        verify(refreshTokenRepository)
                .save(argThat(token ->
                        token.getUser().equals(user)
                                && token.getTokenHash()
                                .equals("hashed-refresh-token")
                                && token.getExpiresAt()
                                .toEpochMilli()
                                == expiresAt.toEpochMilli()
                                && !token.isRevoked()
                ));
    }

    // ============================================================
    // REFRESH TOKEN TESTS
    // ============================================================

    @Test
    void shouldRejectWhenTokenIsNotARefreshToken() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        when(jwtService.isRefreshToken("invalid-token"))
                .thenReturn(false);

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authenticationService.refreshAccessToken(
                        "invalid-token"
                )
        );

        verify(jwtService)
                .isRefreshToken("invalid-token");

        verifyNoInteractions(
                refreshTokenRepository,
                userRepository,
                customUserDetailsService
        );
    }

    @Test
    void shouldRejectWhenRefreshTokenIsNotStored() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        when(jwtService.isRefreshToken("refresh-token"))
                .thenReturn(true);

        when(jwtService.hashToken("refresh-token"))
                .thenReturn("hashed-token");

        when(refreshTokenRepository.findByTokenHash("hashed-token"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authenticationService.refreshAccessToken(
                        "refresh-token"
                )
        );

        verify(jwtService)
                .isRefreshToken("refresh-token");

        verify(jwtService)
                .hashToken("refresh-token");

        verify(refreshTokenRepository)
                .findByTokenHash("hashed-token");
    }

    @Test
    void shouldRejectRevokedRefreshToken() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        User user = new User(
                UUID.randomUUID(),
                "testuser",
                "testuser@example.com",
                "encoded-password",
                "Test",
                "User",
                UserStatus.ACTIVE,
                true,
                Instant.now(),
                Instant.now()
        );

        RefreshToken refreshToken =
                new RefreshToken(
                        user,
                        "hashed-token",
                        Instant.now().plusSeconds(3600),
                        Instant.now()
                );

        refreshToken.revoke();

        when(jwtService.isRefreshToken("refresh-token"))
                .thenReturn(true);

        when(jwtService.hashToken("refresh-token"))
                .thenReturn("hashed-token");

        when(refreshTokenRepository.findByTokenHash("hashed-token"))
                .thenReturn(Optional.of(refreshToken));

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authenticationService.refreshAccessToken(
                        "refresh-token"
                )
        );

        verify(refreshTokenRepository)
                .findByTokenHash("hashed-token");

        verifyNoInteractions(
                customUserDetailsService
        );
    }

    @Test
    void shouldRejectExpiredRefreshToken() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        User user = new User(
                UUID.randomUUID(),
                "testuser",
                "testuser@example.com",
                "encoded-password",
                "Test",
                "User",
                UserStatus.ACTIVE,
                true,
                Instant.now(),
                Instant.now()
        );

        RefreshToken refreshToken =
                new RefreshToken(
                        user,
                        "hashed-token",
                        Instant.now().minusSeconds(60),
                        Instant.now().minusSeconds(3600)
                );

        when(jwtService.isRefreshToken("refresh-token"))
                .thenReturn(true);

        when(jwtService.hashToken("refresh-token"))
                .thenReturn("hashed-token");

        when(refreshTokenRepository.findByTokenHash("hashed-token"))
                .thenReturn(Optional.of(refreshToken));

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authenticationService.refreshAccessToken(
                        "refresh-token"
                )
        );

        verify(refreshTokenRepository)
                .findByTokenHash("hashed-token");

        verifyNoInteractions(
                customUserDetailsService
        );
    }

    @Test
    void shouldRejectRefreshTokenWhenJwtValidationFails() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        User user = new User(
                UUID.randomUUID(),
                "testuser",
                "testuser@example.com",
                "encoded-password",
                "Test",
                "User",
                UserStatus.ACTIVE,
                true,
                Instant.now(),
                Instant.now()
        );

        RefreshToken refreshToken =
                new RefreshToken(
                        user,
                        "hashed-token",
                        Instant.now().plusSeconds(3600),
                        Instant.now()
                );

        CustomUserDetails userDetails =
                new CustomUserDetails(user);

        when(jwtService.isRefreshToken("refresh-token"))
                .thenReturn(true);

        when(jwtService.hashToken("refresh-token"))
                .thenReturn("hashed-token");

        when(refreshTokenRepository.findByTokenHash("hashed-token"))
                .thenReturn(Optional.of(refreshToken));

        when(jwtService.extractUsername("refresh-token"))
                .thenReturn("testuser@example.com");

        when(customUserDetailsService.loadUserByUsername(
                "testuser@example.com"
        )).thenReturn(userDetails);

        when(jwtService.isTokenValid(
                "refresh-token",
                userDetails
        )).thenReturn(false);

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authenticationService.refreshAccessToken(
                        "refresh-token"
                )
        );

        verify(jwtService)
                .isTokenValid(
                        "refresh-token",
                        userDetails
                );

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }

    @Test
    void shouldRotateRefreshTokenSuccessfully() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        User user = new User(
                UUID.randomUUID(),
                "testuser",
                "testuser@example.com",
                "encoded-password",
                "Test",
                "User",
                UserStatus.ACTIVE,
                true,
                Instant.now(),
                Instant.now()
        );

        RefreshToken oldRefreshToken =
                new RefreshToken(
                        user,
                        "old-hash",
                        Instant.now().plusSeconds(3600),
                        Instant.now()
                );

        CustomUserDetails userDetails =
                new CustomUserDetails(user);

        Instant newExpiration =
                Instant.now().plusSeconds(604800);

        when(jwtService.isRefreshToken("old-refresh-token"))
                .thenReturn(true);

        when(jwtService.hashToken("old-refresh-token"))
                .thenReturn("old-hash");

        when(refreshTokenRepository.findByTokenHash("old-hash"))
                .thenReturn(Optional.of(oldRefreshToken));

        when(jwtService.extractUsername("old-refresh-token"))
                .thenReturn("testuser@example.com");

        when(customUserDetailsService.loadUserByUsername(
                "testuser@example.com"
        )).thenReturn(userDetails);

        when(jwtService.isTokenValid(
                "old-refresh-token",
                userDetails
        )).thenReturn(true);

        when(jwtService.generateAccessToken(userDetails))
                .thenReturn("new-access-token");

        when(jwtService.generateRefreshToken(userDetails))
                .thenReturn("new-refresh-token");

        when(jwtService.hashToken("new-refresh-token"))
                .thenReturn("new-hash");

        when(jwtService.extractExpiration("new-refresh-token"))
                .thenReturn(Date.from(newExpiration));

        LoginResponse response =
                authenticationService.refreshAccessToken(
                        "old-refresh-token"
                );

        assertNotNull(response);

        assertEquals(
                "new-access-token",
                response.accessToken()
        );

        assertEquals(
                "new-refresh-token",
                response.refreshToken()
        );

        assertEquals(
                "Bearer",
                response.tokenType()
        );

        assertEquals(
                900,
                response.expiresIn()
        );

        assertTrue(oldRefreshToken.isRevoked());

        assertNotNull(oldRefreshToken.getRevokedAt());

        verify(jwtService)
                .generateAccessToken(userDetails);

        verify(jwtService)
                .generateRefreshToken(userDetails);

        verify(jwtService)
                .hashToken("new-refresh-token");

        verify(jwtService)
                .extractExpiration("new-refresh-token");

        verify(refreshTokenRepository, times(1))
                .save(any(RefreshToken.class));

        verify(refreshTokenRepository)
                .save(argThat(token ->
                        token.getUser().equals(user)
                                && token.getTokenHash()
                                .equals("new-hash")
                                && token.getExpiresAt()
                                .toEpochMilli()
                                == newExpiration.toEpochMilli()
                                && !token.isRevoked()
                ));
    }

        // ============================================================
    // LOGOUT TESTS
    // ============================================================

    @Test
    void shouldRejectLogoutWhenTokenIsNotARefreshToken() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        when(jwtService.isRefreshToken("invalid-token"))
                .thenReturn(false);

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authenticationService.logout("invalid-token")
        );

        verify(jwtService)
                .isRefreshToken("invalid-token");

        verifyNoInteractions(
                refreshTokenRepository
        );
    }

    @Test
    void shouldRejectLogoutWhenRefreshTokenIsNotStored() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        when(jwtService.isRefreshToken("refresh-token"))
                .thenReturn(true);

        when(jwtService.hashToken("refresh-token"))
                .thenReturn("hashed-token");

        when(refreshTokenRepository.findByTokenHash("hashed-token"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authenticationService.logout("refresh-token")
        );

        verify(jwtService)
                .isRefreshToken("refresh-token");

        verify(jwtService)
                .hashToken("refresh-token");

        verify(refreshTokenRepository)
                .findByTokenHash("hashed-token");

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }

    @Test
    void shouldRejectLogoutWhenRefreshTokenIsAlreadyRevoked() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        User user = new User(
                UUID.randomUUID(),
                "testuser",
                "testuser@example.com",
                "encoded-password",
                "Test",
                "User",
                UserStatus.ACTIVE,
                true,
                Instant.now(),
                Instant.now()
        );

        RefreshToken refreshToken =
                new RefreshToken(
                        user,
                        "hashed-token",
                        Instant.now().plusSeconds(3600),
                        Instant.now()
                );

        refreshToken.revoke();

        when(jwtService.isRefreshToken("refresh-token"))
                .thenReturn(true);

        when(jwtService.hashToken("refresh-token"))
                .thenReturn("hashed-token");

        when(refreshTokenRepository.findByTokenHash("hashed-token"))
                .thenReturn(Optional.of(refreshToken));

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authenticationService.logout("refresh-token")
        );

        verify(refreshTokenRepository)
                .findByTokenHash("hashed-token");

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }

    @Test
    void shouldLogoutSuccessfullyAndRevokeRefreshToken() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        ActivationTokenService activationTokenService =
                mock(ActivationTokenService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService customUserDetailsService =
                mock(CustomUserDetailsService.class);
        RefreshTokenRepository refreshTokenRepository =
                mock(RefreshTokenRepository.class);
        IdentityEventPublisher identityEventPublisher =
                mock(IdentityEventPublisher.class);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        passwordEncoder,
                        activationTokenService,
                        roleRepository,
                        authenticationManager,
                        jwtService,
                        customUserDetailsService,
                        refreshTokenRepository,
                        identityEventPublisher
                );

        User user = new User(
                UUID.randomUUID(),
                "testuser",
                "testuser@example.com",
                "encoded-password",
                "Test",
                "User",
                UserStatus.ACTIVE,
                true,
                Instant.now(),
                Instant.now()
        );

        RefreshToken refreshToken =
                new RefreshToken(
                        user,
                        "hashed-token",
                        Instant.now().plusSeconds(3600),
                        Instant.now()
                );

        when(jwtService.isRefreshToken("refresh-token"))
                .thenReturn(true);

        when(jwtService.hashToken("refresh-token"))
                .thenReturn("hashed-token");

        when(refreshTokenRepository.findByTokenHash("hashed-token"))
                .thenReturn(Optional.of(refreshToken));

        authenticationService.logout("refresh-token");

        assertTrue(refreshToken.isRevoked());
        assertNotNull(refreshToken.getRevokedAt());

        verify(jwtService)
                .isRefreshToken("refresh-token");

        verify(jwtService)
                .hashToken("refresh-token");

        verify(refreshTokenRepository)
                .findByTokenHash("hashed-token");

        verify(refreshTokenRepository)
                .save(refreshToken);
    }
}