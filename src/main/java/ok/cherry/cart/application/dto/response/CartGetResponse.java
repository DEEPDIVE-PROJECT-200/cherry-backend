package ok.cherry.cart.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "장바구니 상품 조회 응답 DTO")
public record CartGetResponse(

	@Schema(description = "장바구니에 담긴 상품 리스트")
	List<CartInfoResponse> carts,

	@Schema(description = "장바구니에 담긴 상품의 총합 가격", example = "10000.00")
	BigDecimal totalPrice
) {
}
