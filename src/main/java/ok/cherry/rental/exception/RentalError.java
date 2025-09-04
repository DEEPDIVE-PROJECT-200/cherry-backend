package ok.cherry.rental.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum RentalError implements ErrorCode {

	INVALID_RENTAL_NUMBER("대여 번호가 유효하지 않습니다", HttpStatus.BAD_REQUEST, "R_001");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
