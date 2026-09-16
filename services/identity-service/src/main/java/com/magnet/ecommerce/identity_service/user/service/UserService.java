package com.magnet.ecommerce.identity_service.user.service;

import com.magnet.ecommerce.identity_service.entity.Role;
import com.magnet.ecommerce.identity_service.entity.User;
import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;
import com.magnet.ecommerce.identity_service.repository.RoleRepository;
import com.magnet.ecommerce.identity_service.repository.UserRepository;
import com.magnet.ecommerce.identity_service.user.dto.CreateUserRequest;
import com.magnet.ecommerce.identity_service.user.dto.UpdateUserRequest;
import com.magnet.ecommerce.identity_service.user.dto.UserDeleteResponse;
import com.magnet.ecommerce.identity_service.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.magnet.ecommerce.identity_service.user.dto.UpdateUserStatusRequest;
import com.magnet.ecommerce.identity_service.user.dto.UserRolesResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found: " + id
                        )
                );

        return toResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "User already exists with email: " + request.email()
            );
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException(
                    "Username already exists: " + request.username()
            );
        }

        Role role = roleRepository.findByName(request.role())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found: " + request.role()
                        )
                );

        Instant now = Instant.now();

        User user = new User(
                UUID.randomUUID(),
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                UserStatus.ACTIVE,
                true,
                now,
                now
        );

        user.addRole(role);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(
            UUID id,
            UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found: " + id
                        )
                );

        if (!user.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {

            throw new IllegalArgumentException(
                    "User already exists with email: " + request.email()
            );
        }

        if (!user.getUsername().equals(request.username())
                && userRepository.existsByUsername(request.username())) {

            throw new IllegalArgumentException(
                    "Username already exists: " + request.username()
            );
        }

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUpdatedAt(Instant.now());

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    @Transactional
    public UserDeleteResponse deleteUser(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found: " + id
                        )
                );

        userRepository.delete(user);

        return new UserDeleteResponse(
                "User deleted successfully",
                id
        );
    }

    private UserResponse toResponse(User user) {

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .sorted()
                .toList();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus(),
                user.isEnabled(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Transactional
public UserResponse updateUserStatus(
        UUID id,
        UpdateUserStatusRequest request) {

    User user = userRepository.findById(id)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "User not found: " + id
                    )
            );

    UserStatus newStatus = request.status();

    user.setStatus(newStatus);
    user.setEnabled(newStatus == UserStatus.ACTIVE);
    user.setUpdatedAt(Instant.now());

    User savedUser = userRepository.save(user);

    return toResponse(savedUser);
}
public UserRolesResponse getUserRoles(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found: " + userId
                        )
                );
    
        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .sorted()
                .toList();
    
        return new UserRolesResponse(
                "User roles retrieved successfully",
                user.getId(),
                null,
                roles
        );
    }
    @Transactional
public UserRolesResponse assignRole(
        UUID userId,
        String roleName) {

    User user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "User not found: " + userId
                    )
            );

    Role role = roleRepository.findByName(roleName)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Role not found: " + roleName
                    )
            );

    if (user.getRoles().contains(role)) {
        throw new IllegalArgumentException(
                "User already has role: " + roleName
        );
    }

    user.addRole(role);
    user.setUpdatedAt(Instant.now());

    User savedUser = userRepository.save(user);

    List<String> roles = savedUser.getRoles()
            .stream()
            .map(Role::getName)
            .sorted()
            .toList();

            return new UserRolesResponse(
                "Role assigned successfully",
                savedUser.getId(),
                role.getName(),
                roles
        );
}
@Transactional
public UserRolesResponse removeRole(
        UUID userId,
        String roleName) {

    User user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "User not found: " + userId
                    )
            );

    Role role = roleRepository.findByName(roleName)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Role not found: " + roleName
                    )
            );

    if (!user.getRoles().contains(role)) {
        throw new IllegalArgumentException(
                "User does not have role: " + roleName
        );
    }

    if (user.getRoles().size() == 1) {
        throw new IllegalArgumentException(
                "User must have at least one role"
        );
    }

    user.removeRole(role);
    user.setUpdatedAt(Instant.now());

    User savedUser = userRepository.save(user);

    List<String> roles = savedUser.getRoles()
            .stream()
            .map(Role::getName)
            .sorted()
            .toList();

            return new UserRolesResponse(
                "Role removed successfully",
                savedUser.getId(),
                role.getName(),
                roles
        );
}
}