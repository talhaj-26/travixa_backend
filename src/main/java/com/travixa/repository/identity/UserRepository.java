package com.travixa.repository.identity;

import com.travixa.entity.identity_access.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Boolean existsByUserName(String userName);

    Boolean existsByEmail(String email);

    Page<User> findByUserNameContainingIgnoreCaseOrUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String searchTerm, String searchTerm1, String searchTerm2, String searchTerm3, Pageable pageable);

    boolean existsByUserNameIgnoreCase(String userName);

    boolean existsByEmailIgnoreCase(String email);

    long countByEnabledTrue();


    @Query("""
        select count(distinct u)
        from User u
        join u.roles r
        where upper(r.name) = upper(:role)
        """)
    long countUsersByRoleName(@Param("role") String role);
}
