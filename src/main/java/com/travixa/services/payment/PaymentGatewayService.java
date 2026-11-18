package com.travixa.services.payment;

import com.travixa.entity.payment_financial.PaymentAttempt;
import com.travixa.repository.payment.PaymentAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PaymentGatewayService {

    private final PaymentAttemptRepository paymentAttemptRepository;

    public PaymentGatewayService(PaymentAttemptRepository paymentAttemptRepository) {
        this.paymentAttemptRepository = paymentAttemptRepository;
    }

    public List<PaymentAttempt> findAll() {
        return paymentAttemptRepository.findAll();
    }

    public Optional<PaymentAttempt> findById(Long id) {
        return paymentAttemptRepository.findById(id);
    }

    @Transactional
    public PaymentAttempt save(PaymentAttempt paymentAttempt) {
        return paymentAttemptRepository.save(paymentAttempt);
    }
}
