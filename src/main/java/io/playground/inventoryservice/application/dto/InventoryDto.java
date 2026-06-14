package io.playground.inventoryservice.application.inventory.dto;

import lombok.Builder;

public class InventoryDto {
    @Builder
    public record ReservationRequestInfo(
            Long variantId,
            Integer quantity
    ) {
    }
}
