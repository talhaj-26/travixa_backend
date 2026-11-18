
package com.travixa.controllers;

import com.travixa.dto.user.UserCreateRequest;
import com.travixa.dto.user.UserResponse;
import com.travixa.dto.user.UserStatusUpdateRequest;
import com.travixa.dto.user.UserUpdateRequest;
import com.travixa.entity.identity_access.Role;
import com.travixa.entity.identity_access.User;
import com.travixa.mapper.UserMapper;
import com.travixa.repository.identity.RefreshTokenRepository;
import com.travixa.repository.identity.RoleRepository;
import com.travixa.services.authentication_identity.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserController(
            UserService userService,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            RefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public java.util.List<UserResponse> findAll(Authentication authentication) {
        return userService.findAll().stream()
                .filter(user -> canManageTarget(authentication, user))
                .map(UserMapper::toResponse)
                .toList();
    }

    @GetMapping("/{username}")
    @Transactional(readOnly = true)
    public UserResponse findByUsername(
            @PathVariable String username,
            Authentication authentication) {
        return UserMapper.toResponse(getManageableUser(username, authentication));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody UserCreateRequest request,
            Authentication authentication) {

        Set<String> requestedRoles = request.roles() == null
                ? Set.of("CUSTOMER")
                : request.roles();

        if (requestedRoles.isEmpty()) {
            requestedRoles = Set.of("CUSTOMER");
        }

        boolean requestsAdminRole = requestedRoles.stream()
                .anyMatch(role -> "ADMIN".equals(role) || "SUPER_ADMIN".equals(role));

        if (requestsAdminRole && !isSuperAdmin(authentication)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only SUPER_ADMIN can assign administrator roles");
        }

        if (userService.findByUsername(request.username()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Username is already in use");
        }

        if (userService.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email is already in use");
        }

        User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()));

        user.setUserFirstName(request.firstName());
        user.setUserLastName(request.lastName());
        user.setRoles(resolveRoles(requestedRoles));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserMapper.toResponse(userService.save(user)));
    }

    @PutMapping("/{username}")
    @Transactional
    public UserResponse update(
            @PathVariable String username,
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication) {

        User user = getManageableUser(username, authentication);

        if (request.username() != null
                && !username.equals(request.username())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Username cannot be changed");
        }

        if (request.firstName() != null) {
            user.setUserFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.setUserLastName(request.lastName());
        }

        if (request.email() != null
                && !request.email().equalsIgnoreCase(user.getEmail())
                && userService.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email is already in use");
        }

        if (request.email() != null) {
            user.setEmail(request.email());
        }

        return UserMapper.toResponse(userService.save(user));
    }

    @PatchMapping("/{username}/status")
    @Transactional
    public UserResponse updateStatus(
            @PathVariable String username,
            @Valid @RequestBody UserStatusUpdateRequest request,
            Authentication authentication) {

        User user = getManageableUser(username, authentication);

        if (username.equals(authentication.getName())
                && !request.enabled()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You cannot disable your own account");
        }

        user.setEnabled(request.enabled());
        return UserMapper.toResponse(userService.save(user));
    }

    @DeleteMapping("/{username}")
    @Transactional
    public ResponseEntity<Void> delete(
            @PathVariable String username,
            Authentication authentication) {

        User user = getManageableUser(username, authentication);

        if (username.equals(authentication.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You cannot delete your own account");
        }

        if (hasRole(user, "SUPER_ADMIN")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "SUPER_ADMIN accounts cannot be deleted here");
        }

        refreshTokenRepository.deleteAllByUser_UserName(user.getUserName());
        userService.deleteById(user.getUserName());

        return ResponseEntity.noContent().build();
    }

    private User getManageableUser(
            String username,
            Authentication authentication) {

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        if (!canManageTarget(authentication, user)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to manage this account");
        }

        return user;
    }

    private boolean canManageTarget(
            Authentication authentication,
            User target) {
        return isSuperAdmin(authentication)
                || (!hasRole(target, "ADMIN")
                && !hasRole(target, "SUPER_ADMIN"));
    }

    private boolean isSuperAdmin(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(a -> "SUPER_ADMIN".equals(a.getAuthority()));
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles() != null
                && user.getRoles().stream()
                .anyMatch(role -> roleName.equals(role.getName()));
    }

    private Set<Role> resolveRoles(Set<String> requestedRoles) {
        return requestedRoles.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Unknown role: " + name)))
                .collect(Collectors.toSet());
    }
}
