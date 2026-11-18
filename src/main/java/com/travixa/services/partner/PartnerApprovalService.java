package com.travixa.services.partner;

import com.travixa.entity.partner_management.PartnerApproval;
import com.travixa.repository.partner.PartnerApprovalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PartnerApprovalService {

    private final PartnerApprovalRepository partnerApprovalRepository;

    public PartnerApprovalService(PartnerApprovalRepository partnerApprovalRepository) {
        this.partnerApprovalRepository = partnerApprovalRepository;
    }

    public List<PartnerApproval> findAll() {
        return partnerApprovalRepository.findAll();
    }

    public Optional<PartnerApproval> findById(Long id) {
        return partnerApprovalRepository.findById(id);
    }

    @Transactional
    public PartnerApproval save(PartnerApproval partnerApproval) {
        return partnerApprovalRepository.save(partnerApproval);
    }
}
