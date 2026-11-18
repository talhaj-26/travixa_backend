package com.travixa.controllers;

import com.travixa.dto.payment.InvoiceResponse;
import com.travixa.entity.payment_financial.Invoice;
import com.travixa.mapper.InvoiceMapper;
import com.travixa.services.payment.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<InvoiceResponse> findAll(Authentication authentication) {
        List<Invoice> invoices = hasAdminAuthority(authentication)
                ? invoiceService.findAll()
                : invoiceService.findAllByCustomer(authentication.getName());
        return invoices.stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public InvoiceResponse findById(@PathVariable Long id, Authentication authentication) {
        return (hasAdminAuthority(authentication)
                ? invoiceService.findById(id)
                : invoiceService.findByIdAndCustomer(id, authentication.getName())).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id, Authentication authentication) {
        Invoice invoice = (hasAdminAuthority(authentication)
                ? invoiceService.findById(id)
                : invoiceService.findByIdAndCustomer(id, authentication.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        byte[] pdf = invoiceService.generatePdf(invoice);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + invoice.getInvoiceNumber() + ".pdf\"")
                .body(pdf);
    }

    private boolean hasAdminAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(granted ->
                granted.getAuthority().equals("ADMIN") || granted.getAuthority().equals("SUPER_ADMIN"));
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return InvoiceMapper.toResponse(invoice);
    }
}
