package ok.cherry.cart.application.dto.response;

public record CartAddResponse(
	Long cartId,
	String message
) {

	public static CartAddResponse of(Long cartId) {
		return new CartAddResponse(cartId, "장바구니에 상품이 성공적으로 담겼습니다");
	}
}
