package com.travixa.services.platform;

import com.travixa.entity.platform_cms_support.SupportTicket;
import com.travixa.repository.platform.SupportTicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SupportService {

    private final SupportTicketRepository supportTicketRepository;

    public SupportService(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    public List<SupportTicket> findAll() {
        return supportTicketRepository.findAll();
    }

    public Optional<SupportTicket> findById(Long id) {
        return supportTicketRepository.findById(id);
    }

    @Transactional
    public SupportTicket save(SupportTicket supportTicket) {
        return supportTicketRepository.save(supportTicket);
    }
}
