package ok.cherry.cart.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "장바구니 상품 추가 응답 DTO")
public record CartAddResponse(

	@Schema(description = "추가된 장바구니 상품 Id", example = "1")
	Long cartId,

	@Schema(description = "응답 메세지", example = "장바구니에 상품이 성공적으로 담겼습니다")
	String message
) {

	public static CartAddResponse of(Long cartId) {
		return new CartAddResponse(cartId, "장바구니에 상품이 성공적으로 담겼습니다");
	}
}
