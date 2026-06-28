package io.playground.inventoryservice.infrastructure.persistence.stock;

import io.playground.inventoryservice.application.dto.InventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StockJdbcTemplate {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public int updateQuantityForReserve(InventoryDto.RequestInfo requestInfo) {
        return jdbcTemplate.update(
                "UPDATE stocks SET " +
                        "reserved_quantity = reserved_quantity + :quantity " +
                        "WHERE variant_id = :variantId AND " +
                        "(total_quantity - reserved_quantity) >= :quantity",
                new MapSqlParameterSource()
                        .addValue("variantId", requestInfo.variantId())
                        .addValue("quantity", requestInfo.quantity())
        );
    }

    public int[] updateQuantitiesForReserve(List<InventoryDto.RequestInfo> requestInfos) {
        return jdbcTemplate.batchUpdate(
                "UPDATE stocks SET " +
                        "reserved_quantity = reserved_quantity + :quantity " +
                    "WHERE variant_id = :variantId AND " +
                        "(total_quantity - reserved_quantity) >= :quantity",
                requestInfos.stream()
                        .map(info -> new MapSqlParameterSource()
                                .addValue("variantId", info.variantId())
                                .addValue("quantity", info.quantity()))
                        .toArray(SqlParameterSource[]::new)
        );
    }

    public int[] updateQuantitiesForConfirm(List<InventoryDto.RequestInfo> requestInfos) {
        return jdbcTemplate.batchUpdate(
                "UPDATE stocks SET " +
                        "total_quantity = total_quantity - :quantity, " +
                        "reserved_quantity = reserved_quantity - :quantity " +
                    "WHERE variant_id = :variantId AND " +
                        "total_quantity >= :quantity AND " +
                        "reserved_quantity >= :quantity",
                requestInfos.stream()
                        .map(info -> new MapSqlParameterSource()
                                .addValue("variantId", info.variantId())
                                .addValue("quantity", info.quantity()))
                        .toArray(SqlParameterSource[]::new)
        );
    }

    public int[] updateQuantitiesForRelease(List<InventoryDto.RequestInfo> requestInfos) {
        return jdbcTemplate.batchUpdate(
                "UPDATE stocks SET " +
                        "reserved_quantity = reserved_quantity - :quantity " +
                    "WHERE variant_id = :variantId AND " +
                        "reserved_quantity >= :quantity",
                requestInfos.stream()
                        .map(info -> new MapSqlParameterSource()
                                .addValue("variantId", info.variantId())
                                .addValue("quantity", info.quantity()))
                        .toArray(SqlParameterSource[]::new)
        );
    }

    public int[] updateQuantitiesForRestore(List<InventoryDto.RequestInfo> requestInfos) {
        return jdbcTemplate.batchUpdate(
                "UPDATE stocks SET " +
                        "total_quantity = total_quantity + :quantity " +
                    "WHERE variant_id = :variantId",
                requestInfos.stream()
                        .map(info -> new MapSqlParameterSource()
                                .addValue("variantId", info.variantId())
                                .addValue("quantity", info.quantity()))
                        .toArray(SqlParameterSource[]::new)
        );
    }
}
