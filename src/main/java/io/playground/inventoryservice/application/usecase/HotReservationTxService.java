package io.playground.inventoryservice.application.usecase;

import io.playground.inventoryservice.application.dto.InventoryDto;
import io.playground.inventoryservice.application.port.ReservationPersistencePort;
import io.playground.inventoryservice.application.port.StockPersistencePort;
import io.playground.inventoryservice.domain.Reservation;
import io.playground.inventoryservice.exception.BusinessDetailException;
import io.playground.inventoryservice.exception.BusinessErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HotReservationTxService {
    private final StockPersistencePort stockPersistence;
    private final ReservationPersistencePort reservationPersistence;
    private final StockService stockService;

    /**
     * 재고 단건 예약
     *
     * @param orderExternalId 주문번호
     * @param requestInfo {옵션 ID, 예약 수량}
     */
    @Transactional
    public void reserveStock(String orderExternalId,
                             InventoryDto.RequestInfo requestInfo) {
        // 재고 예약 시도
        if (!stockPersistence
                .updateQuantityForReserve(requestInfo))
            throw new BusinessDetailException(
                    BusinessErrorCode.RESERVATION_RESERVE_FAILED,
                    "OUT_OF_STOCK_DB"
            );

        // 예약 정보 저장
        reservationPersistence.save(
                Reservation.of(
                        null,
                        stockService.getStockId(requestInfo.variantId()),
                        orderExternalId,
                        requestInfo.variantId(),
                        requestInfo.quantity(),
                        Reservation.ReservationStatus.RESERVED,
                        0,
                        null
                )
        );
    }
}
