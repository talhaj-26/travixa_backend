package com.travixa.mapper;

import com.travixa.dto.engagement.ReviewCreateRequest;
import com.travixa.dto.engagement.ReviewResponse;
import com.travixa.entity.customer_engagement.Review;
import com.travixa.entity.identity_access.User;

import java.util.Objects;

public final class ReviewMapper {

    private ReviewMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Review toEntity(ReviewCreateRequest request, User user) {
        Review review = new Review();
        review.setUser(Objects.requireNonNull(user, "user must not be null"));
        review.setTargetType(request.targetType());
        review.setTargetId(request.targetId());
        review.setRating(request.rating());
        review.setTitle(request.title());
        review.setComment(request.comment());
        return review;
    }

    public static ReviewResponse toResponse(Review review) {
        return new ReviewResponse(review.getId(), review.getTargetType(), review.getTargetId(), review.getRating(),
                review.getTitle(), review.getComment(), review.getStatus());
    }
}
