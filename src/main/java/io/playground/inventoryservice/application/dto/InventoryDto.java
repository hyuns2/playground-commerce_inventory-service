package io.playground.inventoryservice.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class InventoryDto {
    @Builder
    public record RequestInfo(
            @NotNull
            Long variantId,
            @NotNull
            Integer quantity
    ) {
    }
}
