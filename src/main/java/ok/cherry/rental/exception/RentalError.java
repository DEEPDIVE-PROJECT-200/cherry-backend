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
	INVALID_RENTAL_PERIOD("대여 시작일이 종료일보다 늦을 수 없습니다", HttpStatus.BAD_REQUEST, "R_003");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
