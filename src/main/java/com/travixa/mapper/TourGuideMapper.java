package com.travixa.mapper;

import com.travixa.dto.tour.TourGuideProfileRequest;
import com.travixa.dto.tour.TourGuideResponse;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.entity.tour_guide.TourGuideProfile;

import java.util.Objects;

public final class TourGuideMapper {

    private TourGuideMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static TourGuideProfile toEntity(TourGuideProfileRequest request, User user,
            PartnerOrganization organization) {
        TourGuideProfile profile = new TourGuideProfile();
        profile.setUser(Objects.requireNonNull(user, "user must not be null"));
        profile.setOrganization(organization);
        updateEntity(profile, request);
        return profile;
    }

    public static void updateEntity(TourGuideProfile profile, TourGuideProfileRequest request) {
        profile.setBiography(request.biography());
        profile.setLicenseNumber(request.licenseNumber());
        profile.setLanguageCodes(request.languageCodes());
        profile.setStatus(request.status());
    }

    public static TourGuideResponse toResponse(TourGuideProfile profile) {
        return new TourGuideResponse(profile.getId(), profile.getUser().getUserName(),
                profile.getOrganization() == null ? null : profile.getOrganization().getId(),
                profile.getBiography(), profile.getLicenseNumber(), profile.getLanguageCodes(), profile.getStatus());
    }
}
