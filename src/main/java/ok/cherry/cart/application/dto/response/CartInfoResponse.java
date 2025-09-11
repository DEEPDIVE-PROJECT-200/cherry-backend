package ok.cherry.cart.application.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import ok.cherry.product.domain.type.Color;

@Schema(description = "장바구니 상품 정보 응답 DTO")
public record CartInfoResponse(

	@Schema(description = "장바구니 Id", example = "1")
	Long cartId,

	@Schema(description = "상품 Id", example = "1")
	Long productId,

	@Schema(description = "상품명", example = "WH-1000XM5")
	String productName,

	@Schema(description = "상품 썸네일 이미지 url", example = "21c559ed-e104-44c0-a027-8db0a3036457_01.jpg")
	String productThumbnailUrl,

	@Schema(description = "상품 색상 옵션", example = "BLACK")
	Color color,

	@Schema(description = "상품 일일 대여 가격", example = "10000.00")
	BigDecimal dailyRentalPrice
) {
}
