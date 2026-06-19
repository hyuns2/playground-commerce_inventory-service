package io.playground.inventoryservice.infrastructure.persistence.stock;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockJpaRepository extends JpaRepository<StockEntity, Long> {
    List<StockEntity> findAllByProductId(Long productId);

    Optional<StockEntity> findByVariantId(Long variantId);

    List<StockEntity> findAllByVariantIdIn(List<Long> variantIds);
}
