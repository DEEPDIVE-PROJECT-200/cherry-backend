package ok.cherry.shipping.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ShippingError implements ErrorCode {

	INVALID_TRACKING_NUMBER("배송 번호가 유효하지 않습니다", HttpStatus.BAD_REQUEST, "S_001"),
	SHIPPING_NOT_FOUND("배송을 찾을 수 없습니다", HttpStatus.NOT_FOUND, "S_002"),
	NOT_PENDING("배송 준비 상태가 아닙니다", HttpStatus.BAD_REQUEST, "S_003"),
	NOT_IN_DELIVERY("배송 중이 아닙니다", HttpStatus.BAD_REQUEST, "S_004");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
