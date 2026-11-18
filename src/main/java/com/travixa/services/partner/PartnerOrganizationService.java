package com.travixa.services.partner;

import com.travixa.dto.partner.PartnerOrganizationRequest;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.mapper.PartnerOrganizationMapper;
import com.travixa.repository.partner.PartnerOrganizationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PartnerOrganizationService {

    private final PartnerOrganizationRepository partnerOrganizationRepository;

    public PartnerOrganizationService(PartnerOrganizationRepository partnerOrganizationRepository) {
        this.partnerOrganizationRepository = partnerOrganizationRepository;
    }

    public List<PartnerOrganization> findAll() {
        return partnerOrganizationRepository.findAll();
    }

    public Optional<PartnerOrganization> findById(Long id) {
        return partnerOrganizationRepository.findById(id);
    }

    @Transactional
    public PartnerOrganization save(PartnerOrganization partnerOrganization) {
        return partnerOrganizationRepository.save(partnerOrganization);
    }

    @Transactional
    public PartnerOrganization create(PartnerOrganizationRequest request) {
        String slug = request.slug().trim().toLowerCase();
        if (partnerOrganizationRepository.existsBySlug(slug)) throw conflict("Partner organization slug already exists");
        if (request.registrationNumber() != null && !request.registrationNumber().isBlank()
                && partnerOrganizationRepository.existsByRegistrationNumber(request.registrationNumber().trim())) {
            throw conflict("Registration number already exists");
        }
        PartnerOrganization organization = PartnerOrganizationMapper.toEntity(request);
        organization.setSlug(slug);
        return persist(organization);
    }

    @Transactional
    public PartnerOrganization update(Long id, PartnerOrganizationRequest request) {
        PartnerOrganization organization = partnerOrganizationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partner organization not found"));
        String slug = request.slug().trim().toLowerCase();
        if (partnerOrganizationRepository.existsBySlugAndIdNot(slug, id)) throw conflict("Partner organization slug already exists");
        if (request.registrationNumber() != null && !request.registrationNumber().isBlank()
                && partnerOrganizationRepository.existsByRegistrationNumberAndIdNot(request.registrationNumber().trim(), id)) {
            throw conflict("Registration number already exists");
        }
        PartnerOrganizationMapper.updateEntity(organization, request);
        organization.setSlug(slug);
        return persist(organization);
    }

    @Transactional
    public void delete(Long id) {
        PartnerOrganization organization = partnerOrganizationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partner organization not found"));
        try {
            partnerOrganizationRepository.delete(organization);
            partnerOrganizationRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete this organization because it is linked to listings or memberships", ex);
        }
    }

    private PartnerOrganization persist(PartnerOrganization organization) {
        try {
            return partnerOrganizationRepository.saveAndFlush(organization);
        } catch (DataIntegrityViolationException ex) {
            throw conflict("Partner organization conflicts with an existing slug or registration number");
        }
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
