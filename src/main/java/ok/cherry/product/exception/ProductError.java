package ok.cherry.product.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ProductError implements ErrorCode {

	INVALID_PRODUCT_NAME("상품명은 필수입니다", HttpStatus.BAD_REQUEST, "P_001"),
	INVALID_BRAND("지원하지 않는 브랜드입니다.", HttpStatus.BAD_REQUEST, "P_002"),
	INVALID_COLOR("지원하지 않는 색상 옵션입니다.", HttpStatus.BAD_REQUEST, "P_003"),
	INVALID_DAILY_RENTAL_PRICE("일일 대여 가격은 0보다 커야 합니다", HttpStatus.BAD_REQUEST, "P_004"),
	INVALID_IMAGE_URL("이미지 URL이 유효하지 않습니다", HttpStatus.BAD_REQUEST, "P_005"),
	INVALID_DISPLAY_ORDER("표시 순서는 0 이상이어야 합니다", HttpStatus.BAD_REQUEST, "P_006"),
	DUPLICATE_PRODUCT("동일한 브랜드, 모델의 상품이 이미 존재합니다", HttpStatus.CONFLICT, "P_007"),
	PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다", HttpStatus.NOT_FOUND, "P_008"),
	IMAGE_URL_IS_NOT_EMPTY("이미지 URL은 비어있을 수 없습니다", HttpStatus.BAD_REQUEST, "P_009"),
	DISPLAY_ORDER_IS_NOT_EMPTY("이미지 표시 순서는 비어있을 수 없습니다", HttpStatus.BAD_REQUEST, "P_010");

	private final String message;
	private final HttpStatus status;
	private final String code;
}
