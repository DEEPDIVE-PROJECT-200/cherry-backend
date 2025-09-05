package ok.cherry.cart.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ok.cherry.product.domain.type.Color;

@Schema(description = "장바구니 상품 추가 요청 DTO")
public record CartCreateRequest(

	@Schema(description = "장바구니에 추가하려는 상품의 Id", example = "1")
	@NotNull(message = "상품 Id는 필수입니다")
	Long productId,

	@Schema(description = "장바구니에 추가하려는 상품의 색상 옵션", example = "BLACK")
	@NotNull(message = "상품 색상 옵션은 필수입니다")
	Color color
) {
}
