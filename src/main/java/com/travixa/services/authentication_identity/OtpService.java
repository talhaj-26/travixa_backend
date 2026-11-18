package com.travixa.services.authentication_identity;

import com.travixa.entity.identity_access.OtpChallenge;
import com.travixa.repository.identity.OtpChallengeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OtpService {

    private final OtpChallengeRepository otpChallengeRepository;

    public OtpService(OtpChallengeRepository otpChallengeRepository) {
        this.otpChallengeRepository = otpChallengeRepository;
    }

    public List<OtpChallenge> findAll() {
        return otpChallengeRepository.findAll();
    }

    public Optional<OtpChallenge> findById(Long id) {
        return otpChallengeRepository.findById(id);
    }

    @Transactional
    public OtpChallenge save(OtpChallenge otpChallenge) {
        return otpChallengeRepository.save(otpChallenge);
    }

    @Transactional
    public void deleteById(Long id) {
        otpChallengeRepository.deleteById(id);
    }
}
