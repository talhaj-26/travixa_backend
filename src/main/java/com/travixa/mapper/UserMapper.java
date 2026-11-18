package com.travixa.mapper;

import com.travixa.dto.user.UserProfileResponse;
import com.travixa.dto.user.UserResponse;
import com.travixa.dto.user.UserUpdateRequest;
import com.travixa.entity.identity_access.User;
import com.travixa.exceptions.InvalidRequestException;

import java.util.Set;
import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse(user.getUserName(), user.getUserName(), user.getUserFirstName(),
                user.getUserLastName(), user.getEmail());
    }

    public static UserResponse toResponse(User user) {
        Set<String> roles = user.getRoles() == null ? Set.of()
                : user.getRoles().stream()
                        .filter(role -> role != null)
                        .map(role -> role.getName())
                        .filter(roleName -> roleName != null && !roleName.isBlank())
                        .collect(Collectors.toUnmodifiableSet());
        return new UserResponse(user.getUserName(), user.getUserName(), user.getUserFirstName(),
                user.getUserLastName(), user.getEmail(), user.getEnabled(), roles, user.getDateCreated(),
                user.getLastUpdated());
    }

    public static void updateEntity(User user, UserUpdateRequest request) {
        if (request.username() != null && !request.username().equals(user.getUserName())) {
            throw new InvalidRequestException("Username cannot be changed");
        }
        if (request.firstName() != null) {
            user.setUserFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setUserLastName(request.lastName());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
    }
}
