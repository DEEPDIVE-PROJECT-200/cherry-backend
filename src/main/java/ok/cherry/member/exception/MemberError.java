package ok.cherry.member.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum MemberError implements ErrorCode {

	NOT_ACTIVE("활성 상태가 아닙니다", HttpStatus.CONFLICT, "M_001"),
	INVALID_NICKNAME("닉네임은 2~10글자만 가능합니다", HttpStatus.BAD_REQUEST, "M_002"),
	INVALID_EMAIL("이메일 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST, "M_003"),
	USER_NOT_FOUND("사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND, "M_004"),
	ALREADY_REGISTERED("이미 회원가입한 회원입니다", HttpStatus.CONFLICT, "M_005"),
	DUPLICATE_EMAIL("이미 사용 중인 이메일입니다", HttpStatus.CONFLICT, "M_006"),
	DUPLICATE_NICKNAME("이미 사용 중인 닉네임입니다", HttpStatus.CONFLICT, "M_007");

	private final String message;
	private final HttpStatus status;
	private final String code;
}