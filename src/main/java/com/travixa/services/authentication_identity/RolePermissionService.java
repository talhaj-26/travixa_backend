package com.travixa.services.authentication_identity;

import com.travixa.enums.ERole;
import com.travixa.entity.identity_access.Role;
import com.travixa.entity.identity_access.User;
import com.travixa.repository.identity.RoleRepository;
import com.travixa.repository.identity.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

@Component
public class RolePermissionService implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RolePermissionService(JdbcTemplate jdbcTemplate, UserRepository userRepository,
            RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (ERole role : ERole.values()) {
            jdbcTemplate.update("insert into roles (name) values (?) on conflict (name) do nothing", role.name());
        }

        Boolean legacyRoleTableExists = jdbcTemplate.queryForObject(
                "select to_regclass(current_schema() || '.user_roles') is not null", Boolean.class);
        if (Boolean.TRUE.equals(legacyRoleTableExists)) {
            jdbcTemplate.update("""
                    insert into user_role_assignments (user_name, role_id)
                    select legacy.user_name, role.id
                    from user_roles legacy
                    join roles role on role.name = legacy.role_name
                    on conflict (user_name, role_id) do nothing
                    """);
        }

        bootstrapSuperAdmin();
    }

    private void bootstrapSuperAdmin() {
        if (!Boolean.parseBoolean(System.getenv("TRAVIXA_BOOTSTRAP_ADMIN_ENABLED"))) {
            return;
        }

        String username = requiredEnvironmentValue("TRAVIXA_BOOTSTRAP_ADMIN_USERNAME").trim();
        String email = requiredEnvironmentValue("TRAVIXA_BOOTSTRAP_ADMIN_EMAIL").trim().toLowerCase(Locale.ROOT);
        String password = requiredEnvironmentValue("TRAVIXA_BOOTSTRAP_ADMIN_PASSWORD");
        if (username.isEmpty() || email.isEmpty() || password.length() < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException("Bootstrap administrator environment values are invalid");
        }

        // Never modify an existing account: a collision requires operator intervention.
        if (userRepository.existsByUserNameIgnoreCase(username)
                || userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        Role superAdminRole = roleRepository.findByName(ERole.SUPER_ADMIN.name())
                .orElseThrow(() -> new IllegalStateException("SUPER_ADMIN role is unavailable"));
        User user = new User(username, email, passwordEncoder.encode(password));
        user.setRoles(Set.of(superAdminRole));
        userRepository.save(user);
    }

    private String requiredEnvironmentValue(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required bootstrap administrator environment value: " + name);
        }
        return value;
    }
}
