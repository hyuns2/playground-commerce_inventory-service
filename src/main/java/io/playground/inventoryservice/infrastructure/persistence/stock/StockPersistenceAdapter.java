package io.playground.inventoryservice.infrastructure.persistence.stock;

import io.playground.inventoryservice.application.dto.InventoryDto;
import io.playground.inventoryservice.application.port.StockPersistencePort;
import io.playground.inventoryservice.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StockPersistenceAdapter implements StockPersistencePort {
    private final StockJpaRepository stockRepository;
    private final StockJdbcTemplate stockTemplate;

    @Override
    public List<Stock> findAllByProductId(Long productId) {
        return stockRepository
                .findAllByProductId(productId).stream()
                .map(StockEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Stock> findByVariantId(Long variantId) {
        return stockRepository
                .findByVariantId(variantId)
                .map(StockEntity::toDomain);
    }

    @Override
    public List<Stock> findAllByVariantIds(List<Long> variantIds) {
        return stockRepository
                .findAllByVariantIdIn(variantIds).stream()
                .map(StockEntity::toDomain)
                .toList();
    }

    @Override
    public int[] updateQuantitiesForReserve(List<InventoryDto.RequestInfo> requestInfos) {
        return stockTemplate.updateQuantitiesForReserve(
                requestInfos
        );
    }

    @Override
    public boolean updateQuantitiesForConfirm(List<InventoryDto.RequestInfo> requestInfos) {
        return Arrays.stream(
                stockTemplate.updateQuantitiesForConfirm(
                        requestInfos
                )
        ).allMatch(v -> v > 0);
    }

    @Override
    public boolean updateQuantitiesForRelease(List<InventoryDto.RequestInfo> requestInfos) {
        return Arrays.stream(
                stockTemplate.updateQuantitiesForRelease(
                        requestInfos
                )
        ).allMatch(v -> v > 0);
    }

    @Override
    public boolean updateQuantitiesForRestore(List<InventoryDto.RequestInfo> requestInfos) {
        return Arrays.stream(
                stockTemplate.updateQuantitiesForRestore(
                        requestInfos
                )
        ).allMatch(v -> v > 0);
    }
}
