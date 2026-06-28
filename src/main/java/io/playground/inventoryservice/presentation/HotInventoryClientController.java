package io.playground.inventoryservice.presentation;

import io.playground.inventoryservice.application.dto.InventoryDto;
import io.playground.inventoryservice.application.usecase.HotReservationService;
import io.playground.inventoryservice.application.usecase.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hot-client")
@RequiredArgsConstructor
public class HotInventoryClientController {
    private final HotReservationService hotReservationService;
    private final ReservationService reservationService;

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStock(@RequestParam String orderExternalId,
                                             @Valid @NotNull @RequestBody List<InventoryDto.RequestInfo> requestInfos) {
        hotReservationService.reserveStocks(
                orderExternalId,
                requestInfos
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
    public ResponseEntity<Void> restoreStocks(@RequestParam String orderExternalId) {
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
