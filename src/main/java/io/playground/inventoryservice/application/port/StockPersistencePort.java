package io.playground.inventoryservice.application.port;

import io.playground.inventoryservice.application.dto.InventoryDto;
import io.playground.inventoryservice.domain.Stock;

import java.util.List;
import java.util.Optional;

public interface StockPersistencePort {
    List<Stock> findAllByProductId(Long productId);

    Optional<Stock> findByVariantId(Long variantId);

    List<Stock> findAllByVariantIds(List<Long> variantIds);

    boolean updateQuantityForReserve(InventoryDto.RequestInfo requestInfo);

    int[] updateQuantitiesForReserve(List<InventoryDto.RequestInfo> requestInfos);

    boolean updateQuantitiesForConfirm(List<InventoryDto.RequestInfo> requestInfos);

    boolean updateQuantitiesForRelease(List<InventoryDto.RequestInfo> requestInfos);

    boolean updateQuantitiesForRestore(List<InventoryDto.RequestInfo> requestInfos);
}
