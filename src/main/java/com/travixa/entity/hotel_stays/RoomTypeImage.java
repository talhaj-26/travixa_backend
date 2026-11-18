package com.travixa.entity.hotel_stays;

import com.travixa.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room_type_images", indexes = {
        @Index(name = "idx_room_type_images_type_order", columnList = "room_type_id, display_order")
})
@Getter
@Setter
@NoArgsConstructor
public class RoomTypeImage extends BaseAuditEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @NotBlank
    @Size(max = 500)
    @Column(name = "storage_key", nullable = false, length = 500)
    private String storageKey;

    @Size(max = 160)
    @Column(name = "alt_text", length = 160)
    private String altText;

    @PositiveOrZero
    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private boolean coverImage;
}
