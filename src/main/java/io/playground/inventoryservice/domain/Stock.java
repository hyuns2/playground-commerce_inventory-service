package io.playground.inventoryservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Stock {
    private Long id;

    private Long variantId;

    private Long productId;

    private int totalQuantity;

    private int reservedQuantity;

    public static Stock of(Long id,
                           Long variantId,
                           Long productId,
                           int totalQuantity,
                           int reservedQuantity) {
        return new Stock(id, variantId, productId, totalQuantity, reservedQuantity);
    }

    public int getAvailableQuantity() {
        return this.getTotalQuantity() -
                this.getReservedQuantity();
    }
}
