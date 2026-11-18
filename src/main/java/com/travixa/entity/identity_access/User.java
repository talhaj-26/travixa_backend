package com.travixa.entity.identity_access;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travixa.enums.ERole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Table(name = "USERS")
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    private String userName;

    @Column
    private String userFirstName;

    @Column
    private String userLastName;

    @Column
    @JsonIgnore
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    // Initialize all Boolean fields with default values
    @Column
    private Boolean enabled = true;

    @Column
    private Boolean credentialsNonExpired = true;

    @Column
    private Boolean accountNonExpired = true;

    @Column
    private Boolean accountNonLocked = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role_assignments", joinColumns = @JoinColumn(name = "user_name"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant lastUpdated;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        // Initialize Booleans in constructor too
        this.enabled = true;
        this.credentialsNonExpired = true;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
    }

    public User() {
        // Initialize in default constructor
        this.enabled = true;
        this.credentialsNonExpired = true;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
    }

}