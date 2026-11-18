package com.travixa.controllers;

import com.travixa.dto.engagement.ReviewResponse;
import com.travixa.entity.customer_engagement.Review;
import com.travixa.mapper.ReviewMapper;
import com.travixa.services.customer.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewResponse> findAll() {
        return reviewService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ReviewResponse findById(@PathVariable Long id) {
        return reviewService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewMapper.toResponse(review);
    }
}
