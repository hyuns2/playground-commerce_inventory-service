package io.playground.inventoryservice.application.usecase;

import io.playground.inventoryservice.application.port.StockPersistencePort;
import io.playground.inventoryservice.domain.Stock;
import io.playground.inventoryservice.exception.BusinessErrorCode;
import io.playground.inventoryservice.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockPersistencePort stockPersistence;

    /**
     * 특정 상품의 모든 옵션 재고 조회
     *
     * @param productId 상품 ID
     */
    @Transactional(readOnly = true)
    public List<Stock> getStocks(Long productId) {
        return stockPersistence.findAllByProductId(productId);
    }

    /**
     * 단일 옵션 재고 조회
     *
     * @param variantId 옵션 ID
     */
    @Transactional(readOnly = true)
    public Stock getStock(Long variantId) {
        return stockPersistence.findByVariantId(variantId)
                .orElseThrow(() -> new BusinessException(
                        BusinessErrorCode.STOCK_NOT_FOUND
                ));
    }
}
