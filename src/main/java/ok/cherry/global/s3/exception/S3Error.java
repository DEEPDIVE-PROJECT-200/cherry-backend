package ok.cherry.global.s3.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum S3Error implements ErrorCode {

	UPLOAD_FAIL("파일 업로드에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR, "U_001"),
	DELETE_FAIL("파일 삭제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR, "U_002"),
	EMPTY_FILE_LIST("파일이 비어있습니다.", HttpStatus.BAD_REQUEST, "U_003"),
	INVALID_FILE("파일이 유효하지 않습니다.", HttpStatus.BAD_REQUEST, "U_004"),
	INVALID_FILE_URL("파일 URL이 유효하지 않습니다.", HttpStatus.BAD_REQUEST, "U_005"),
	INVALID_FILE_TYPE("유효하지 않은 파일 타입입니다.", HttpStatus.BAD_REQUEST, "U_006"),
	FILE_NOT_FOUND("파일이 존재하지 않습니다.", HttpStatus.NOT_FOUND, "U_007"),
	DUPLICATE_FILE("이미 존재하는 파일입니다.", HttpStatus.CONFLICT, "U_008");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
