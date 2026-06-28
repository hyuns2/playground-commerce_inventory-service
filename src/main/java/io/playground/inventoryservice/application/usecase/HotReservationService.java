package io.playground.inventoryservice.application.usecase;

import io.playground.inventoryservice.application.dto.InventoryDto;
import io.playground.inventoryservice.application.port.ReservationPersistencePort;
import io.playground.inventoryservice.application.port.StockPersistencePort;
import io.playground.inventoryservice.domain.Stock;
import io.playground.inventoryservice.exception.BusinessDetailException;
import io.playground.inventoryservice.exception.BusinessErrorCode;
import io.playground.inventoryservice.infrastructure.worker.hotinventory.HotInventoryWorkerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotReservationService {
    private final RedisScript<Long> stockScript;
    private final StockPersistencePort stockPersistence;
    private final ReservationPersistencePort reservationPersistence;
    private final HotReservationTxService hotReservationTxService;
    private final StringRedisTemplate stringRedisTemplate;
    private final HotInventoryWorkerProperties properties;

    /**
     * 재고 수량을 조회하는 캐시의 키 반환
     *
     * @param variantId 상품옵션 ID
     * @return 캐시의 키
     */
    public String getAvailableKey(Long variantId) {
        return properties.getCacheName() + "::variant:" + variantId + ":available:";
    }

    /**
     * 핫딜 재고 예약
     *
     * @param orderExternalId 주문번호
     * @param requestInfos {옵션 ID, 예약 수량}
     */
    public void reserveStocks(String orderExternalId,
                              List<InventoryDto.RequestInfo> requestInfos) {
        if (reservationPersistence.existsByOrderExternalId(orderExternalId))
            return;

        if (requestInfos.size() != 1)
            throw new BusinessDetailException(
                    BusinessErrorCode.RESERVATION_RESERVE_FAILED,
                    "ONLY_SUPPORT_SINGLE_VARIANT"
            );
        InventoryDto.RequestInfo requestInfo = requestInfos.get(0);

        long result = stringRedisTemplate.execute(
                stockScript,
                List.of(
                        getAvailableKey(requestInfo.variantId())
                ),
                Integer.toString(
                        requestInfo.quantity()
                )
        );

        if (result == -1L)
            throw new BusinessDetailException(
                    BusinessErrorCode.STOCK_NOT_FOUND,
                    "CACHE_WARMING_REQUIRED"
            );
        else if (result == 0L)
            throw new BusinessDetailException(
                    BusinessErrorCode.RESERVATION_RESERVE_FAILED,
                    "OUT_OF_STOCK_CACHE"
            );
        else
            hotReservationTxService.reserveStock(
                    orderExternalId,
                    requestInfo
            );
    }

    /**
     * 재고 재캐싱
     *
     * @param variantIds 상품옵션 ID 리스트
     */
    public void recacheStocks(List<Long> variantIds) {
        for (Stock stock :
                stockPersistence.findAllByVariantIds(variantIds))
            stringRedisTemplate.opsForValue()
                    .set(
                            getAvailableKey(
                                    stock.getVariantId()
                            ),
                            String.valueOf(Math.round(
                                    stock.getAvailableQuantity() *
                                            properties.getFactor()
                            ))
                    );
    }
}
