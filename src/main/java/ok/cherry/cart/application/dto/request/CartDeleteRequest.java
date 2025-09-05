package ok.cherry.cart.application.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record CartDeleteRequest(
	@NotBlank(message = "최소 하나 이상의 장바구니 Id는 필수입니다")
	List<Long> cartIds
) {
}
