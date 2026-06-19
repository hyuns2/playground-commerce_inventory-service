package io.playground.inventoryservice.infrastructure.persistence.reservation;

import io.playground.inventoryservice.domain.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ReservationJdbcTemplate {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public int[] saveAll(List<Reservation> reservations) {
        return jdbcTemplate.batchUpdate(
                "INSERT INTO reservations " +
                        "(id, stock_id, variant_id, order_external_id, quantity, restored_quantity, status) " +
                    "VALUES (:id, :stockId, :variantId, :orderExternalId, :quantity, :restoredQuantity, :status)",
                reservations.stream()
                        .map(r -> new MapSqlParameterSource()
                                .addValue("id", r.getId())
                                .addValue("stockId", r.getStockId())
                                .addValue("variantId", r.getVariantId())
                                .addValue("orderExternalId", r.getOrderExternalId())
                                .addValue("quantity", r.getQuantity())
                                .addValue("restoredQuantity", r.getRestoredQuantity())
                                .addValue("status", r.getStatus().name())
                        )
                        .toArray(SqlParameterSource[]::new)
        );
    }

    public int[] updateStatusByIds(List<Long> ids,
                                   Reservation.ReservationStatus reservationStatus,
                                   Reservation.ReservationStatus beforeStatus) {
        return jdbcTemplate.batchUpdate(
                "UPDATE reservations SET " +
                            "status = :status " +
                    "WHERE id = :id AND " +
                            "status = :beforeStatus",
                ids.stream()
                        .map(i -> new MapSqlParameterSource()
                                .addValue("id", i)
                                .addValue("status", reservationStatus.name())
                                .addValue("beforeStatus", beforeStatus.name())
                        ).toArray(SqlParameterSource[]::new)
        );
    }

    public int[] updateForAllRestoration(Map<Long, Integer> reservationQuantities) {
        return jdbcTemplate.batchUpdate(
                "UPDATE reservations SET " +
                        "restored_quantity = restored_quantity + :quantity, " +
                        "status = 'RESTORED' " +
                    "WHERE id = :id AND " +
                        "status = 'CONFIRMED'",
                reservationQuantities.entrySet().stream()
                        .map(entry -> new MapSqlParameterSource()
                                .addValue("id", entry.getKey())
                                .addValue("quantity", entry.getValue())
                        ).toArray(SqlParameterSource[]::new)
        );
    }

    public int[] updateForPartialRestoration(Map<Long, Integer> reservationQuantities,
                                             String idempotencyKey) {
        return jdbcTemplate.batchUpdate(
                "UPDATE reservations SET " +
                        "status = 'PARTIAL_RESTORED', " +
                        "restored_quantity = restored_quantity + :quantity, " +
                        "last_idempotency_key = :idempotencyKey " +
                    "WHERE id = :id AND " +
                        "(status = 'CONFIRMED' OR status = 'PARTIAL_RESTORED') AND " +
                        "quantity >= restored_quantity + :quantity",
                reservationQuantities.entrySet().stream()
                        .map(entry -> new MapSqlParameterSource()
                                .addValue("id", entry.getKey())
                                .addValue("quantity", entry.getValue())
                                .addValue("idempotencyKey", idempotencyKey)
                        ).toArray(SqlParameterSource[]::new)
        );
    }
}
