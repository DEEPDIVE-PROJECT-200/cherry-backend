package ok.cherry.product.application.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 조회 응답 DTO")
public record ProductSearchResponse(

	@Schema(description = "대여 가능한 상품 정보 목록")
	List<ProductThumbnailResponse> products,

	@Schema(description = "다음 페이지 존재 여부")
	boolean hasNext,

	@Schema(description = "마지막 상품 ID")
	Long lastProductId
) {
	public static ProductSearchResponse of(
		List<ProductThumbnailResponse> products,
		boolean hasNext,
		Long lastProductId
	) {
		return new ProductSearchResponse(products, hasNext, lastProductId);
	}
}