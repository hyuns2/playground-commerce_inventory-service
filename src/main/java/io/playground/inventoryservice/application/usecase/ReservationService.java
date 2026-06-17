package io.playground.inventoryservice.application.usecase;

import io.playground.inventoryservice.application.dto.InventoryDto;
import io.playground.inventoryservice.application.port.ReservationPersistencePort;
import io.playground.inventoryservice.application.port.StockPersistencePort;
import io.playground.inventoryservice.domain.Reservation;
import io.playground.inventoryservice.domain.Stock;
import io.playground.inventoryservice.exception.BusinessDetailException;
import io.playground.inventoryservice.exception.BusinessErrorCode;
import io.playground.inventoryservice.infrastructure.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final StockPersistencePort stockPersistence;
    private final ReservationPersistencePort reservationPersistence;
    private final JsonUtil jsonUtil;

    /**
     * 재고 다건 예약
     *
     * @param orderExternalId 주문번호
     * @param requestInfos {옵션 ID, 예약 수량} 리스트
     */
    @Transactional
    public void reserveStocks(String orderExternalId,
                              List<InventoryDto.RequestInfo> requestInfos) {
        if (reservationPersistence.existsByOrderExternalId(orderExternalId))
            return;

        // 옵션 ID -> 재고 맵핑
        Map<Long, Stock> stockByVariantId = stockPersistence
                .findAllByVariantIds(
                        requestInfos.stream()
                                .map(InventoryDto.RequestInfo::variantId)
                                .toList()
                ).stream()
                .collect(Collectors.toMap(
                        Stock::getVariantId,
                        stock -> stock
                ));

        // 재고 예약 시도
        int[] updatedResult = stockPersistence
                .updateQuantitiesForReserve(requestInfos);

        // 예약 실패한 옵션이 있다면, 옵션 ID와 가능 수량을 맵핑하여 예외 반환
        Map<Long, Integer> unavailable = new HashMap<>();
        for (int i = 0; i < updatedResult.length; i++)
            if (updatedResult[i] < 1)
                unavailable.put(
                        requestInfos.get(i).variantId(),
                        stockByVariantId.get(
                                requestInfos.get(i).variantId()
                        ).getAvailableQuantity()
                );

        if (!unavailable.isEmpty())
            throw new BusinessDetailException(
                    BusinessErrorCode.RESERVATION_RESERVE_FAILED,
                    jsonUtil.toJson(unavailable)
            );

        // 모두 예약 가능한 경우, 예약 정보 저장
        if (reservationPersistence.saveAll(
                requestInfos.stream()
                        .map(req -> Reservation.of(
                                null,
                                stockByVariantId.get(req.variantId()).getId(),
                                orderExternalId,
                                req.variantId(),
                                req.quantity(),
                                Reservation.ReservationStatus.RESERVED,
                                0,
                                null
                        )).toList()
        ).contains(0))
            throw new BusinessDetailException(
                    BusinessErrorCode.RESERVATION_RESERVE_FAILED,
                    "RESERVATION_CREATE_FAILED"
            );
    }

    /**
     * 재고 다건 확정
     *
     * @param orderExternalId 주문번호
     */
    @Transactional
    public void confirmStocks(String orderExternalId) {
        // 예약 상태인 정보만 처리 (멱등성 보장)
        List<Reservation> reservations = reservationPersistence
                .findAllByOrderExternalIdAndStatus(
                        orderExternalId,
                        Reservation.ReservationStatus.RESERVED
                );

        if (reservations.isEmpty())
            return;

        // 재고 확정 시도 -> 성공한 경우에만 예약 상태 변경
        if (
                !stockPersistence.updateQuantitiesForConfirm(
                        reservations.stream()
                                .map(r ->
                                        InventoryDto.RequestInfo.builder()
                                                .variantId(r.getVariantId())
                                                .quantity(r.getQuantity())
                                                .build()
                                )
                                .sorted(Comparator.comparing(InventoryDto.RequestInfo::variantId))
                                .toList()
                ) ||
                !reservationPersistence.updateStatusByIds(
                        reservations.stream()
                                .map(Reservation::getId)
                                .sorted(Comparator.comparing(Long::longValue))
                                .toList(),
                        Reservation.ReservationStatus.CONFIRMED,
                        Reservation.ReservationStatus.RESERVED
                )
        )
            throw new BusinessDetailException(
                    BusinessErrorCode.RESERVATION_CONFIRM_FAILED,
                    "INVALID_STATUS"
            );
    }

    /**
     * 재고 다건 예약 해제
     *
     * @param orderExternalId 주문번호
     */
    @Transactional
    public void releaseStocks(String orderExternalId) {
        // 예약 상태인 정보만 처리 (멱등성 보장)
        List<Reservation> reservations = reservationPersistence
                .findAllByOrderExternalIdAndStatus(
                        orderExternalId,
                        Reservation.ReservationStatus.RESERVED
                );

        if (reservations.isEmpty())
            return;

        // 예약 해제 시도 -> 성공한 경우에만 예약 상태 변경
        if (
                !stockPersistence.updateQuantitiesForRelease(
                        reservations.stream()
                                .map(r ->
                                        InventoryDto.RequestInfo.builder()
                                                .variantId(r.getVariantId())
                                                .quantity(r.getQuantity())
                                                .build()
                                ).sorted(Comparator.comparing(InventoryDto.RequestInfo::variantId))
                                .toList()
                ) ||
                !reservationPersistence.updateStatusByIds(
                        reservations.stream()
                                .map(Reservation::getId)
                                .sorted(Comparator.comparing(Long::longValue))
                                .toList(),
                        Reservation.ReservationStatus.RELEASED,
                        Reservation.ReservationStatus.RESERVED
                )
        )
            throw new BusinessDetailException(
                    BusinessErrorCode.RESERVATION_RELEASE_FAILED,
                    "INVALID_STATUS"
            );
    }

    /**
     * 주문의 모든 재고 복구
     *
     * @param orderExternalId 주문번호
     */
    @Transactional
    public void restoreAllStocks(String orderExternalId) {
        // 확정 상태인 정보만 처리 (멱등성 보장)
        List<Reservation> reservations = reservationPersistence
                .findAllByOrderExternalIdAndStatus(
                        orderExternalId,
                        Reservation.ReservationStatus.CONFIRMED
                );

        if (reservations.isEmpty())
            return;

        // 재고 복구 시도 -> 성공한 경우에만 예약 상태 변경
        if (
                !stockPersistence.updateQuantitiesForRestore(
                        reservations.stream()
                                .map(r ->
                                        InventoryDto.RequestInfo.builder()
                                                .variantId(r.getVariantId())
                                                .quantity(r.getQuantity())
                                                .build()
                                ).sorted(Comparator.comparing(InventoryDto.RequestInfo::variantId))
                                .toList()
                ) ||
                !reservationPersistence.updateForAllRestoration(
                        reservations.stream()
                                .collect(
                                        Collectors.toMap(
                                                Reservation::getId,
                                                Reservation::getQuantity
                                        )
                                )
                )
        )
            throw new BusinessDetailException(
                    BusinessErrorCode.RESERVATION_RESTORE_FAILED,
                    "INVALID_STATUS"
            );
    }

    /**
     * 주문의 재고 부분복구
     *
     * @param idempotencyKey 멱등성 보장키
     * @param orderExternalId 주문번호
     * @param variantQuantities {옵션 ID: 복구 수량} 맵
     */
    @Transactional
    public void restorePartialStocks(String idempotencyKey,
                                     String orderExternalId,
                                     Map<Long, Integer> variantQuantities) {
        variantQuantities.entrySet().stream()
                .filter(entry ->
                        entry.getKey() == null ||
                        entry.getValue() == null ||
                                entry.getValue() < 1
                ).findAny()
                .ifPresent(entry -> {
                    throw new BusinessDetailException(
                            BusinessErrorCode.RESERVATION_PARTIAL_RESTORE_FAILED,
                            "INVALID_VARIANT_QUANTITY"
                    );
                });

        // 확정 또는 부분복구 상태 && 중복 시도가 아닌 정보만 처리 (멱등성 보장)
        List<Reservation> reservations = reservationPersistence
                .findAllByOrderExternalIdAndVariantIdsAndStatuses(
                        orderExternalId,
                        variantQuantities.keySet().stream().toList(),
                        List.of(
                                Reservation.ReservationStatus.CONFIRMED,
                                Reservation.ReservationStatus.PARTIAL_RESTORED
                        )
                ).stream()
                .filter(r -> r.getLastIdempotencyKey() == null ||
                        !r.getLastIdempotencyKey().equals(idempotencyKey)
                ).toList();

        if (reservations.isEmpty())
            return;

        // 재고 복구 시도 -> 성공한 경우에만 예약 상태 변경
        if (
                !stockPersistence.updateQuantitiesForRestore(
                        reservations.stream()
                                .map(r ->
                                        InventoryDto.RequestInfo.builder()
                                                .variantId(r.getVariantId())
                                                .quantity(variantQuantities.get(r.getVariantId()))
                                                .build()
                                ).sorted(Comparator.comparing(InventoryDto.RequestInfo::variantId))
                                .toList()
                ) ||
                !reservationPersistence.updateForPartialRestoration(
                        reservations.stream()
                                .collect(Collectors.toMap(
                                        Reservation::getId,
                                        r -> variantQuantities.get(
                                                r.getVariantId()
                                        )
                                )),
                        idempotencyKey
                )
        )
            throw new BusinessDetailException(
                    BusinessErrorCode.RESERVATION_PARTIAL_RESTORE_FAILED,
                    "INVALID_STATUS"
            );
    }

    /**
     * 재고 상태에 따라, 예약 해제 또는 전체 복구 처리
     *
     * @param orderExternalId 주문번호
     */
    @Transactional
    public void reStocks(String orderExternalId) {
        releaseStocks(orderExternalId);

        restoreAllStocks(orderExternalId);
    }
}
