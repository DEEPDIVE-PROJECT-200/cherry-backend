package ok.cherry.cart.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CartError implements ErrorCode {

	CART_NOT_FOUND("장바구니 상품을 찾을 수 없습니다", HttpStatus.NOT_FOUND, "C_001"),
	UNAUTHORIZED_CART_ACCESS("요청된 장바구니 상품의 소유자 정보가 일치하지 않습니다", HttpStatus.UNAUTHORIZED, "C_002"),
	CART_DELETE_FAIL("장바구니 상품 삭제에 실패하였습니다", HttpStatus.INTERNAL_SERVER_ERROR, "C_003"),
	CART_LIMIT_EXCEEDED("장바구니에는 최대 3개의 상품만 담을 수 있습니다", HttpStatus.BAD_REQUEST, "C_004"),
	DUPLICATE_CART("이미 장바구니에 동일한 상품이 존재합니다", HttpStatus.CONFLICT, "C_005");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
