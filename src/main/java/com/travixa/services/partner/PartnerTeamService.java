package com.travixa.services.partner;

import com.travixa.entity.partner_management.PartnerMembership;
import com.travixa.repository.partner.PartnerMembershipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PartnerTeamService {

    private final PartnerMembershipRepository partnerMembershipRepository;

    public PartnerTeamService(PartnerMembershipRepository partnerMembershipRepository) {
        this.partnerMembershipRepository = partnerMembershipRepository;
    }

    public List<PartnerMembership> findAll() {
        return partnerMembershipRepository.findAll();
    }

    public List<PartnerMembership> findActiveByUserName(String userName) {
        return partnerMembershipRepository.findByUser_UserNameAndActiveTrue(userName);
    }

    public Optional<PartnerMembership> findById(Long id) {
        return partnerMembershipRepository.findById(id);
    }

    @Transactional
    public PartnerMembership save(PartnerMembership partnerMembership) {
        return partnerMembershipRepository.save(partnerMembership);
    }
}
