package ok.cherry.cart.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ok.cherry.product.domain.type.Color;

public record CartAddRequest(

	@NotBlank(message = "상품 Id는 필수입니다")
	Long productId,

	@NotNull(message = "상품 색상 옵션은 필수입니다")
	Color color
) {
}
