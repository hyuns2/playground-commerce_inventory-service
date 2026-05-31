package io.playground.inventoryservice.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.playground.inventoryservice.application.dto.InventoryDto;
import io.playground.inventoryservice.application.usecase.ReservationService;
import io.playground.inventoryservice.application.usecase.StockService;
import io.playground.inventoryservice.domain.Stock;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class InventoryClientController {
    private final StockService stockService;
    private final ReservationService reservationService;

    @GetMapping("/stocks-info")
    public ResponseEntity<Map<Long, Integer>> getStocksInfo(@RequestParam Long productId) {
        return ResponseEntity.ok().body(
                stockService.getStocks(productId).stream()
                        .collect(
                                Collectors.toMap(
                                        Stock::getVariantId,
                                        Stock::getAvailableQuantity
                                )
                        )
        );
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStocks(@RequestParam String orderExternalId,
                                              @NotNull @RequestBody List<InventoryDto.ReservationRequestInfo> reservationRequestInfos) throws JsonProcessingException {
        reservationService.reserveStocks(
                orderExternalId,
                reservationRequestInfos
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmStocks(@RequestParam String orderExternalId) {
        reservationService.confirmStocks(orderExternalId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/release")
    public ResponseEntity<Void> releaseStocks(@RequestParam String orderExternalId) {
        reservationService.releaseStocks(orderExternalId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/restore")
    public ResponseEntity<Void> restoreStocks(@RequestParam String orderExternalId,
                                                 @RequestBody(required = false) List<Long> variantIds) {
        reservationService.restoreAllStocks(
                orderExternalId
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/partial-restore")
    public ResponseEntity<Void> restorePartially(@RequestParam String idempotencyKey,
                                                 @RequestParam String orderExternalId,
                                                 @RequestBody Map<Long, Integer> variantQuantities) {
        reservationService.restorePartialStocks(
                idempotencyKey,
                orderExternalId,
                variantQuantities
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/restock")
    public ResponseEntity<Void> reStocks(@RequestParam String orderExternalId) {
        reservationService.reStocks(
                orderExternalId
        );

        return ResponseEntity.ok().build();
    }
}
