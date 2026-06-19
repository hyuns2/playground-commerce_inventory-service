package io.playground.inventoryservice.infrastructure.persistence.stock;

import io.playground.inventoryservice.domain.Stock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "stocks",
        indexes = {
                @Index(name = "idx_productId", columnList = "productId")
        }
)
public class StockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long variantId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int reservedQuantity;

    public static StockEntity fromDomain(Stock stock) {
        return StockEntity.builder()
                .variantId(stock.getVariantId())
                .productId(stock.getProductId())
                .totalQuantity(stock.getTotalQuantity())
                .reservedQuantity(stock.getReservedQuantity())
                .build();
    }

    public Stock toDomain() {
        return Stock.of(
                this.id,
                this.variantId,
                this.productId,
                this.totalQuantity,
                this.reservedQuantity
        );
    }
}
