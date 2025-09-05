package ok.cherry.cart.application.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "장바구니 상품 삭제 요청 DTO")
public record CartDeleteRequest(

	@Schema(description = "삭제하려는 장바구니 상품 Id", example = "[1,2]")
	@NotEmpty(message = "최소 하나 이상의 장바구니 Id는 필수입니다")
	List<Long> cartIds
) {
}
