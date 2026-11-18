package com.travixa.security;

import com.travixa.entity.identity_access.User;
import com.travixa.entity.identity_access.Role;
import com.travixa.entity.identity_access.Permission;
import com.travixa.repository.identity.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        public CustomUserDetailsService(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User myUser = userRepository.findByUserName(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

                return new CustomUserDetails(
                                myUser.getUserName(),
                                myUser.getPassword(),
                                Boolean.TRUE.equals(myUser.getEnabled()),
                                Boolean.TRUE.equals(myUser.getAccountNonExpired()),
                                Boolean.TRUE.equals(myUser.getCredentialsNonExpired()),
                                Boolean.TRUE.equals(myUser.getAccountNonLocked()),
                                getAuthorities(myUser));
        }

        private Collection<? extends GrantedAuthority> getAuthorities(User user) {
                Set<GrantedAuthority> authorities = new LinkedHashSet<>();
                if (user.getRoles() == null) {
                        return List.of();
                }

                for (Role role : user.getRoles()) {
                        if (role == null) {
                                continue;
                        }
                        addAuthority(authorities, role.getName());
                        if (role.getPermissions() == null) {
                                continue;
                        }
                        for (Permission permission : role.getPermissions()) {
                                if (permission != null) {
                                        addAuthority(authorities, permission.getName());
                                }
                        }
                }
                return new ArrayList<>(authorities);
        }

        private void addAuthority(Set<GrantedAuthority> authorities, String authority) {
                if (authority != null && !authority.isBlank()) {
                        authorities.add(new SimpleGrantedAuthority(authority));
                }
        }
}