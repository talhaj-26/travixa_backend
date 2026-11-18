package com.travixa.entity.customer_engagement;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "review_replies")
@Getter
@Setter
@NoArgsConstructor
public class ReviewReply extends BaseAuditEntity {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false, unique = true)
    private Review review;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_user_name", nullable = false)
    private User author;

    @NotBlank
    @Size(max = 3000)
    @Column(nullable = false, length = 3000)
    private String response;
}
