package ok.cherry.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum TokenError implements ErrorCode {

	INVALID_TOKEN("유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED, "T_001"),
	INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다", HttpStatus.UNAUTHORIZED, "T_002"),
	ALREADY_LOGOUT("이미 로그아웃된 토큰입니다", HttpStatus.UNAUTHORIZED, "T_003"),
	REFRESH_TOKEN_MISMATCH("리프레시 토큰이 일치하지 않습니다", HttpStatus.UNAUTHORIZED, "T_004");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
