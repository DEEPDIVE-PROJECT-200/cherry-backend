package ok.cherry.admin.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum AdminError implements ErrorCode {

	ADMIN_NOT_FOUND("어드민을 찾을 수 없습니다", HttpStatus.NOT_FOUND, "A_001");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
