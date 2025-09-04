package ok.cherry.shipping.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ShippingError implements ErrorCode {

	INVALID_TRACKING_NUMBER("배송 번호가 유효하지 않습니다", HttpStatus.BAD_REQUEST, "S_001");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
