package ok.cherry.member.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum MemberError implements ErrorCode {

	USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "M_001"),
	DUPLICATE_EMAIL("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT, "M_002"),
	INVALID_PASSWORD("비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED, "M_003");

	private final String message;
	private final HttpStatus status;
	private final String code;
}