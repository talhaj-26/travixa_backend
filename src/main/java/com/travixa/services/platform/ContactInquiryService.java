package com.travixa.services.platform;

import com.travixa.entity.platform_cms_support.ContactInquiry;
import com.travixa.repository.platform.ContactInquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ContactInquiryService {

    private final ContactInquiryRepository contactInquiryRepository;

    public ContactInquiryService(ContactInquiryRepository contactInquiryRepository) {
        this.contactInquiryRepository = contactInquiryRepository;
    }

    public List<ContactInquiry> findAll() {
        return contactInquiryRepository.findAll();
    }

    public Optional<ContactInquiry> findById(Long id) {
        return contactInquiryRepository.findById(id);
    }

    @Transactional
    public ContactInquiry save(ContactInquiry contactInquiry) {
        return contactInquiryRepository.save(contactInquiry);
    }
}
