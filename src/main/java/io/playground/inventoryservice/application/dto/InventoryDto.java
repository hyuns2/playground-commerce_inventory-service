package io.playground.inventoryservice.application.dto;

import lombok.Builder;

public class InventoryDto {
    @Builder
    public record ReservationRequestInfo(
            Long variantId,
            Integer quantity
    ) {
    }
}
