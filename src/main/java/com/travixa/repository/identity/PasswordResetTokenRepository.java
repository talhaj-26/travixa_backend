package com.travixa.repository.identity;

import com.travixa.entity.identity_access.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHashAndConsumedAtIsNull(String tokenHash);

    List<PasswordResetToken> findAllByUser_UserNameAndConsumedAtIsNull(String userName);
}
