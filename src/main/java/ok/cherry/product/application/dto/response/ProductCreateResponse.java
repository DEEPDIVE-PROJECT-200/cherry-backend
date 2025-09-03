package ok.cherry.product.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 등록 응답 DTO")
public record ProductCreateResponse(

	@Schema(description = "생성된 상품 ID", example = "1")
	Long productId,

	@Schema(description = "상품 생성 시간", example = "2025-09-03T16:28:02.272339")
	LocalDateTime createdAt,

	@Schema(description = "상품 등록 결과 메시지", example = "상품이 성공적으로 등록되었습니다.")
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
