package ok.cherry.payment.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum PaymentError implements ErrorCode {

	PAYMENT_NOT_FOUND("결제를 찾을 수 없습니다", HttpStatus.NOT_FOUND, "P_001"),
	PAYMENT_ACCESS_DENIED("결제 정보에 접근할 권한이 없습니다", HttpStatus.FORBIDDEN, "P_002");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
