package io.playground.inventoryservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessErrorCode {
    // 400
    RESERVATION_RESERVE_FAILED("INVENTORY-400:001", "재고 예약에 실패했습니다.", HttpStatus.BAD_REQUEST),
    RESERVATION_CONFIRM_FAILED("INVENTORY-400:002", "재고 예약 확정에 실패했습니다.", HttpStatus.BAD_REQUEST),
    RESERVATION_RELEASE_FAILED("INVENTORY-400:003", "재고 예약 해제에 실패했습니다.", HttpStatus.BAD_REQUEST),
    RESERVATION_RESTORE_FAILED("INVENTORY-400:004", "재고 복구에 실패했습니다.", HttpStatus.BAD_REQUEST),
    RESERVATION_PARTIAL_RESTORE_FAILED("INVENTORY-400:005", "재고 부분 복구에 실패했습니다.", HttpStatus.BAD_REQUEST),

    // 404
    STOCK_NOT_FOUND("INVENTORY-404:001", "해당하는 상품 옵션의 재고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 500
    JSON_PROCESSING_FAILED("INVENTORY-500:001", "JSON 직렬화 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
