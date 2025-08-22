package ok.cherry.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum AuthError implements ErrorCode {

	KAKAO_TOKEN_RESPONSE_EMPTY("카카오 토큰 응답이 비어있습니다.", HttpStatus.SERVICE_UNAVAILABLE, "A_001"),
	KAKAO_PROVIDER_ID_EMPTY("카카오 사용자 ID 응답이 비어있습니다. ", HttpStatus.SERVICE_UNAVAILABLE, "A_002");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
