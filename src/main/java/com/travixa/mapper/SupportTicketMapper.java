package com.travixa.mapper;

import com.travixa.dto.support_platform.SupportTicketCreateRequest;
import com.travixa.dto.support_platform.SupportTicketResponse;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.entity.platform_cms_support.SupportTicket;

import java.util.Objects;

public final class SupportTicketMapper {

    private SupportTicketMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static SupportTicket toEntity(SupportTicketCreateRequest request, User customer,
            PartnerOrganization organization, String referenceNumber) {
        SupportTicket ticket = new SupportTicket();
        ticket.setCustomer(Objects.requireNonNull(customer, "customer must not be null"));
        ticket.setOrganization(organization);
        ticket.setReferenceNumber(Objects.requireNonNull(referenceNumber, "referenceNumber must not be null"));
        ticket.setSubject(request.subject());
        ticket.setCategory(request.category());
        ticket.setPriority(request.priority());
        return ticket;
    }

    public static SupportTicketResponse toResponse(SupportTicket ticket) {
        return new SupportTicketResponse(ticket.getId(),
                ticket.getOrganization() == null ? null : ticket.getOrganization().getId(),
                ticket.getReferenceNumber(), ticket.getSubject(), ticket.getCategory(), ticket.getPriority(),
                ticket.getStatus(), ticket.getResolvedAt());
    }
}
