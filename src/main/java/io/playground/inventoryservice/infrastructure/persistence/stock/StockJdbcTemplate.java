package io.playground.inventoryservice.infrastructure.persistence.stock;

import io.playground.inventoryservice.application.dto.InventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StockJdbcTemplate {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public int[] updateQuantitiesForReserve(List<InventoryDto.ReservationRequestInfo> reservationRequestInfos) {
        return jdbcTemplate.batchUpdate(
                "UPDATE stocks SET " +
                        "reserved_quantity = reserved_quantity + :quantity " +
                    "WHERE variant_id = :variantId AND " +
                        "(total_quantity - reserved_quantity) >= :quantity",
                reservationRequestInfos.stream()
                        .map(info -> new MapSqlParameterSource()
                                .addValue("variantId", info.variantId())
                                .addValue("quantity", info.quantity()))
                        .toArray(SqlParameterSource[]::new)
        );
    }

    public int[] updateQuantitiesForConfirm(List<InventoryDto.ReservationRequestInfo> reservationRequestInfos) {
        return jdbcTemplate.batchUpdate(
                "UPDATE stocks SET " +
                        "total_quantity = total_quantity - :quantity, " +
                        "reserved_quantity = reserved_quantity - :quantity " +
                    "WHERE variant_id = :variantId AND " +
                        "total_quantity >= :quantity AND " +
                        "reserved_quantity >= :quantity",
                reservationRequestInfos.stream()
                        .map(info -> new MapSqlParameterSource()
                                .addValue("variantId", info.variantId())
                                .addValue("quantity", info.quantity()))
                        .toArray(SqlParameterSource[]::new)
        );
    }

    public int[] updateQuantitiesForRelease(List<InventoryDto.ReservationRequestInfo> reservationRequestInfos) {
        return jdbcTemplate.batchUpdate(
                "UPDATE stocks SET " +
                        "reserved_quantity = reserved_quantity - :quantity " +
                    "WHERE variant_id = :variantId AND " +
                        "reserved_quantity >= :quantity",
                reservationRequestInfos.stream()
                        .map(info -> new MapSqlParameterSource()
                                .addValue("variantId", info.variantId())
                                .addValue("quantity", info.quantity()))
                        .toArray(SqlParameterSource[]::new)
        );
    }

    public int[] updateQuantitiesForRestore(List<InventoryDto.ReservationRequestInfo> reservationRequestInfos) {
        return jdbcTemplate.batchUpdate(
                "UPDATE stocks SET " +
                        "total_quantity = total_quantity + :quantity " +
                    "WHERE variant_id = :variantId",
                reservationRequestInfos.stream()
                        .map(info -> new MapSqlParameterSource()
                                .addValue("variantId", info.variantId())
                                .addValue("quantity", info.quantity()))
                        .toArray(SqlParameterSource[]::new)
        );
    }
}
