package io.playground.inventoryservice.application.inventory.port;

import io.playground.inventoryservice.application.inventory.dto.InventoryDto;
import io.playground.inventoryservice.domain.Stock;

import java.util.List;
import java.util.Optional;

public interface StockPersistencePort {
    List<Stock> findAllByProductId(Long productId);

    Optional<Stock> findByVariantId(Long variantId);

    List<Stock> findAllByVariantIds(List<Long> variantIds);

    int[] updateQuantitiesForReserve(List<InventoryDto.ReservationRequestInfo> reservationRequestInfos);

    boolean updateQuantitiesForConfirm(List<InventoryDto.ReservationRequestInfo> reservationRequestInfos);

    boolean updateQuantitiesForRelease(List<InventoryDto.ReservationRequestInfo> reservationRequestInfos);

    boolean updateQuantitiesForRestore(List<InventoryDto.ReservationRequestInfo> reservationRequestInfos);
}
