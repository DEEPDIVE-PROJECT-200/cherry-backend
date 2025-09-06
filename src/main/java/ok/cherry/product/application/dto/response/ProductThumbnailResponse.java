package ok.cherry.product.application.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import ok.cherry.product.domain.type.Brand;

@Schema(description = "상품 조회 응답 DTO")
public record ProductThumbnailResponse(

	@Schema(description = "상품 ID", example = "1")
	Long id,

	@Schema(description = "상품명", example = "WH-1000XM5")
	String name,

	@Schema(description = "브랜드명", example = "SONY")
	Brand brand,

	@Schema(description = "상품 대표 썸네일 이미지 URL", example = "21c559ed-e104-44c0-a027-8db0a3036457_01.jpg")
	String thumbnailImageUrl,

	@Schema(description = "일일 대여 가격(원)", example = "5000")
	BigDecimal dailyRentalPrice
) {
}