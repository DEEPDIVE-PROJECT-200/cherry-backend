package ok.cherry.rental.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum RentalError implements ErrorCode {

	INVALID_RENTAL_NUMBER("대여 번호가 유효하지 않습니다", HttpStatus.BAD_REQUEST, "R_001"),
	RENTAL_ITEMS_NOT_EMPTY("대여 상품은 비어있을 수 없습니다", HttpStatus.BAD_REQUEST, "R_002"),
	INVALID_RENTAL_PERIOD("대여 시작일이 종료일보다 늦을 수 없습니다", HttpStatus.BAD_REQUEST, "R_003"),
	INVALID_RENTAL_REQUEST("유효하지 않은 대여 요청입니다", HttpStatus.BAD_REQUEST, "R_004"),
	AMBIGUOUS_RENTAL_REQUEST("단건 대여 주문과 장바구니 주문을 동시에 요청할 수 없습니다", HttpStatus.BAD_REQUEST, "R_005"),
	RENTAL_NOT_FOUND("대여를 찾을 수 없습니다", HttpStatus.NOT_FOUND, "R_006"),
	NOT_PENDING("대여 준비 상태가 아닙니다", HttpStatus.BAD_REQUEST, "R_007"),
	NOT_ACTIVE("대여 중 상태가 아닙니다", HttpStatus.BAD_REQUEST, "R_008"),
	NOT_IN_RETURN("반납 중 상태가 아닙니다", HttpStatus.BAD_REQUEST, "R_009"),
	NOT_COMPLETED("대여 완료 상태가 아닙니다", HttpStatus.BAD_REQUEST, "R_010"),
	NOT_REVIEW_STATUS_AVAILABLE("리뷰를 작성할 수 없습니다", HttpStatus.BAD_REQUEST, "R_011"),
	FORBIDDEN_ACCESS("접근 권한이 없습니다", HttpStatus.FORBIDDEN, "R_012");

	private final String message;
	private final HttpStatus status;
	private final String code;
}