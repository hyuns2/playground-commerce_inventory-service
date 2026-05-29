package io.playground.inventoryservice.presentation;

import io.playground.inventoryservice.application.usecase.StockService;
import io.playground.inventoryservice.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final StockService stockService;

    @GetMapping("/variants")
    public ResponseEntity<List<Stock>> getStocks(@RequestParam Long productId) {
        return ResponseEntity.ok().body(
                stockService.getStocks(productId)
        );
    }

    @GetMapping("/variant")
    public ResponseEntity<Stock> getStock(@RequestParam Long variantId) {
        return ResponseEntity.ok().body(
                stockService.getStock(variantId)
        );
    }
}
