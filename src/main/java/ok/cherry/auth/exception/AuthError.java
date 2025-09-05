package ok.cherry.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum AuthError implements ErrorCode {

	KAKAO_TOKEN_RESPONSE_EMPTY("카카오 토큰 응답이 비어있습니다", HttpStatus.SERVICE_UNAVAILABLE, "A_001"),
	KAKAO_PROVIDER_ID_EMPTY("카카오 사용자 ID 응답이 비어있습니다", HttpStatus.SERVICE_UNAVAILABLE, "A_002"),
	INVALID_OAUTH_STATE("잘못된 OAuth State 매개변수입니다", HttpStatus.BAD_REQUEST, "A_003"),
	USER_NOT_REGISTERED("회원가입이 필요합니다", HttpStatus.BAD_REQUEST, "A_004"),
	INVALID_TEMP_TOKEN("유효하지 않은 임시 토큰입니다", HttpStatus.BAD_REQUEST, "A_005");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
