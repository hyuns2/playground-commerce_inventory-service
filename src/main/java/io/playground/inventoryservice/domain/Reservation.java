package io.playground.inventoryservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Reservation {
    private Long id;

    private Long stockId;

    private String orderExternalId;

    private Long variantId;

    private int quantity;

    private ReservationStatus status;

    private int restoredQuantity;

    private String lastIdempotencyKey;

    public enum ReservationStatus {
        RESERVED, CONFIRMED, RELEASED,
        RESTORED, PARTIAL_RESTORED
    }

    public static Reservation of(Long id,
                                 Long stockId,
                                 String orderExternalId,
                                 Long variantId,
                                 int quantity,
                                 ReservationStatus status,
                                 int restoredQuantity,
                                 String lastIdempotencyKey) {
        return new Reservation(id, stockId, orderExternalId, variantId, quantity, status, restoredQuantity, lastIdempotencyKey);
    }
}
