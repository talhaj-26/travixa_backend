package com.travixa.repository.identity;

import com.travixa.entity.identity_access.OtpChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, Long> {
}
