package ok.cherry.product.application.dto.response;

import java.time.LocalDateTime;

public record ProductCreateResponse(
	Long productId,
	LocalDateTime createdAt,
	String message
) {

	public static ProductCreateResponse of(Long productId) {
		return new ProductCreateResponse(
			productId,
			LocalDateTime.now(),
			"상품이 성공적으로 등록되었습니다."
		);
	}
}
