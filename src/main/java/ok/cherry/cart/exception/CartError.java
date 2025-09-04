package ok.cherry.cart.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CartError implements ErrorCode {

	CART_NOT_FOUND("해당 장바구니를 찾을 수 없습니다", HttpStatus.NOT_FOUND, "C_001");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
