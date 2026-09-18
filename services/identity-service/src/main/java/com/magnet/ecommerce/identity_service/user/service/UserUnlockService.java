package com.magnet.ecommerce.identity_service.user.service;

import com.magnet.ecommerce.identity_service.entity.User;
import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;
import com.magnet.ecommerce.identity_service.repository.UserRepository;
import com.magnet.ecommerce.identity_service.user.dto.UserUnlockResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserUnlockService {

    private final UserRepository userRepository;

    public UserUnlockService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserUnlockResponse unlockAccount(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found: " + email
                        )
                );

        if (user.getStatus() != UserStatus.LOCKED) {
            throw new IllegalStateException(
                    "Account is not locked: " + email
            );
        }

        user.unlockAccount();

        User savedUser = userRepository.save(user);

        return new UserUnlockResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getStatus().name(),
                savedUser.isEnabled(),
                savedUser.getFailedLoginAttempts(),
                savedUser.getLockedAt(),
                "Account unlocked successfully"
        );
    }
}