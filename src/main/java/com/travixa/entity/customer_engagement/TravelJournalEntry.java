package com.travixa.entity.customer_engagement;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import com.travixa.enums.JournalVisibility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "travel_journal_entries")
@Getter
@Setter
@NoArgsConstructor
public class TravelJournalEntry extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_name", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saved_trip_id")
    private SavedTrip savedTrip;

    @NotBlank
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String title;

    @NotBlank
    @Size(max = 10000)
    @Column(nullable = false, length = 10000)
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private JournalVisibility visibility = JournalVisibility.PRIVATE;

    @NotNull
    @Column(nullable = false)
    private Instant recordedAt;
}
