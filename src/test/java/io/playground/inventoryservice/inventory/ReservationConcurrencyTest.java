package io.playground.inventoryservice.inventory;

import io.playground.inventoryservice.application.dto.InventoryDto;
import io.playground.inventoryservice.application.usecase.ReservationService;
import io.playground.inventoryservice.common.BaseIntegrationTest;
import io.playground.inventoryservice.common.ConcurrencyTestUtil;
import io.playground.inventoryservice.infrastructure.persistence.stock.StockEntity;
import io.playground.inventoryservice.infrastructure.persistence.stock.StockJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Slf4j
public class ReservationConcurrencyTest extends BaseIntegrationTest {
    @Autowired
    ReservationService reservationService;
    @Autowired
    StockJpaRepository stockRepository;

    @Test
    @DisplayName("재고 예약 동시성 테스트 -> 재고 10개, 동시 접근 100명")
    void test_reserve() throws InterruptedException {
        // given
        StockEntity stock = stockRepository.save(
                StockEntity.builder()
                        .variantId(1L)
                        .productId(1L)
                        .totalQuantity(10)
                        .reservedQuantity(0)
                        .build()
        );

        // when
        ConcurrencyTestUtil.ConcurrencyResult result =
                ConcurrencyTestUtil.runConcurrently(
                        () -> reservationService.reserveStocks(
                                UUID.randomUUID().toString(),
                                List.of(
                                        InventoryDto.ReservationRequestInfo.builder()
                                                .variantId(stock.getVariantId())
                                                .quantity(1)
                                                .build()
                                )
                        ),
                        100
        );

        // then
        StockEntity finalStock = stockRepository.findById(stock.getId())
                .orElseThrow();

        log.warn("최종 재고 상태: total={}, reserved={}, available={}",
                finalStock.getTotalQuantity(),
                finalStock.getReservedQuantity(),
                finalStock.getTotalQuantity() - finalStock.getReservedQuantity()
        );

        Assertions.assertThat(
                finalStock.getTotalQuantity() == 10).isTrue();
        Assertions.assertThat(
                finalStock.getReservedQuantity() == 10).isTrue();

        Assertions.assertThat(
                result.successCount()).isEqualTo(10);
        Assertions.assertThat(
                result.failureCount()).isEqualTo(90);

        Assertions.assertThat(
                result.errors()).hasSize(90);
        Assertions.assertThat(
                result.errors()
        ).allMatch(e -> e.getMessage().contains("RESERVATION_RESERVE_FAILED"));
    }
}
