package io.playground.inventoryservice.infrastructure.persistence.reservation;

import io.playground.inventoryservice.domain.Reservation;
import io.playground.inventoryservice.infrastructure.persistence.stock.StockEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "reservations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_orderExternalId_variantId",
                columnNames = {"orderExternalId", "variantId"}
        )
)
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private StockEntity stock;

    @Column(nullable = false)
    private String orderExternalId;

    @Column(nullable = false)
    private Long variantId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int restoredQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Reservation.ReservationStatus status;

    public static ReservationEntity fromDomain(Reservation reservation,
                                               StockEntity stockEntity) {
        return ReservationEntity.builder()
                .stock(stockEntity)
                .orderExternalId(reservation.getOrderExternalId())
                .variantId(reservation.getVariantId())
                .quantity(reservation.getQuantity())
                .restoredQuantity(reservation.getRestoredQuantity())
                .status(reservation.getStatus())
                .build();
    }

    public Reservation toDomain() {
        return Reservation.of(
                this.id,
                this.stock.getId(),
                this.orderExternalId,
                this.variantId,
                this.quantity,
                this.restoredQuantity,
                this.status
        );
    }
}
