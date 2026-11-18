package com.travixa.services.partner;

import com.travixa.entity.partner_management.PartnerApplication;
import com.travixa.entity.partner_management.PartnerApproval;
import com.travixa.entity.identity_access.User;
import com.travixa.dto.partner.PartnerApplicationRequest;
import com.travixa.dto.partner.PartnerApplicationReviewRequest;
import com.travixa.enums.PartnerApplicationStatus;
import com.travixa.repository.partner.PartnerApplicationRepository;
import com.travixa.repository.partner.PartnerApprovalRepository;
import com.travixa.repository.identity.UserRepository;
import com.travixa.mapper.PartnerApplicationMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PartnerApplicationService {

    private final PartnerApplicationRepository partnerApplicationRepository;
    private final PartnerApprovalRepository approvalRepository;
    private final UserRepository userRepository;

    public PartnerApplicationService(PartnerApplicationRepository partnerApplicationRepository,
            PartnerApprovalRepository approvalRepository, UserRepository userRepository) {
        this.partnerApplicationRepository = partnerApplicationRepository;
        this.approvalRepository = approvalRepository;
        this.userRepository = userRepository;
    }

    public List<PartnerApplication> findAll() {
        return partnerApplicationRepository.findAll();
    }

    public Optional<PartnerApplication> findById(Long id) {
        return partnerApplicationRepository.findById(id);
    }

    @Transactional
    public PartnerApplication submit(PartnerApplicationRequest request, String username) {
        User applicant = userRepository.findByUserName(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
        PartnerApplication application = PartnerApplicationMapper.toEntity(request, applicant);
        application.setStatus(PartnerApplicationStatus.SUBMITTED);
        application.setSubmittedAt(java.time.Instant.now());
        return partnerApplicationRepository.save(application);
    }

    @Transactional
    public PartnerApplication review(Long id, PartnerApplicationReviewRequest request, String reviewerName) {
        if (request.decision() != PartnerApplicationStatus.APPROVED
                && request.decision() != PartnerApplicationStatus.REJECTED
                && request.decision() != PartnerApplicationStatus.UNDER_REVIEW) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid review decision");
        }
        PartnerApplication application = partnerApplicationRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Partner application not found"));
        if (application.getStatus() != PartnerApplicationStatus.SUBMITTED
                && application.getStatus() != PartnerApplicationStatus.UNDER_REVIEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Application is not awaiting review");
        }
        User reviewer = userRepository.findByUserName(reviewerName).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Reviewer not found"));
        application.setStatus(request.decision());
        PartnerApproval approval = new PartnerApproval();
        approval.setApplication(application);
        approval.setReviewer(reviewer);
        approval.setDecision(request.decision());
        approval.setNotes(request.notes());
        approval.setDecidedAt(java.time.Instant.now());
        approvalRepository.save(approval);
        return partnerApplicationRepository.save(application);
    }

    @Transactional
    public PartnerApplication save(PartnerApplication partnerApplication) {
        return partnerApplicationRepository.save(partnerApplication);
    }
}
