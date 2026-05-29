package io.playground.inventoryservice.infrastructure.persistence.stock;

import io.playground.inventoryservice.application.dto.InventoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockJpaRepository extends JpaRepository<StockEntity, Long> {
    List<StockEntity> findAllByProductId(Long productId);

    Optional<StockEntity> findByVariantId(Long variantId);

    List<StockEntity> findAllByVariantIdIn(List<Long> variantIds);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update StockEntity s " +
            "set s.reservedQuantity = s.reservedQuantity + :quantity " +
            "where s.variantId = :variantId and " +
            "(s.totalQuantity - s.reservedQuantity) >= :quantity")
    int updateQuantitiesForReserve(List<InventoryDto.ReservationRequestInfo> reservationRequestInfos);
}
