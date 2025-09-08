package ok.cherry.rental.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum RentalError implements ErrorCode {

	INVALID_RENTAL_NUMBER("대여 번호가 유효하지 않습니다", HttpStatus.BAD_REQUEST, "R_001"),
	RENTAL_ITEMS_NOT_EMPTY("대여 상품은 비어있을 수 없습니다", HttpStatus.BAD_REQUEST, "R_002"),
	INVALID_RENTAL_PERIOD("대여 시작일이 종료일보다 늦을 수 없습니다", HttpStatus.BAD_REQUEST, "R_003"),
	INVALID_RENTAL_REQUEST("유효하지 않은 대여 요청입니다", HttpStatus.BAD_REQUEST, "R_004"),
	AMBIGUOUS_RENTAL_REQUEST("단건 대여 주문과 장바구니 주문을 동시에 요청할 수 없습니다", HttpStatus.BAD_REQUEST, "R_005");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
