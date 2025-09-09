package ok.cherry.product.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.ProductImageDetail;
import ok.cherry.product.domain.type.Brand;
import ok.cherry.product.domain.type.Color;

@Schema(description = "상품 정보 응답 DTO")
public record ProductInfoResponse(

	@Schema(description = "상품 Id", example = "1")
	Long productId,

	@Schema(description = "상품명", example = "WH-1000XM5")
	String productName,

	@Schema(description = "브랜드명", example = "SONY")
	Brand brand,

	@Schema(description = "일일 대여 가격(원)", example = "5000")
	BigDecimal dailyRentalPrice,

	@Schema(description = "상품 색상 목록", example = "[\"BLACK\", \"GOLD\"]")
	List<Color> colors,

	@Schema(description = "상품 썸네일 이미지 url 리스트",
		example = "[\"21c559ed-e104-44c0-a027-8db0a3036457_01.jpg\", \"7662bbcf-7c90-47cd-9ad9-c8267ba51cc6_02.jpg\"]")
	List<String> productThumbnailUrls,

	@Schema(description = "상품 상세 이미지 url 리스트",
		example = "[\"21c559ed-e104-44c0-a027-8db0a3036457_01.jpg\", \"7662bbcf-7c90-47cd-9ad9-c8267ba51cc6_02.jpg\"]")
	List<String> productImageDetailUrls
) {

	public static ProductInfoResponse of(Product product) {
		List<String> productThumbnailUrls = product.getDetail().getProductImageDetails().stream()
			.map(ProductImageDetail::getImageUrl)
			.toList();

		List<String> productImageDetailUrls = product.getDetail().getProductImageDetails().stream()
			.map(ProductImageDetail::getImageUrl)
			.toList();

		return new ProductInfoResponse(
			product.getId(),
			product.getName(),
			product.getBrand(),
			product.getDailyRentalPrice(),
			product.getColors(),
			productThumbnailUrls,
			productImageDetailUrls
		);
	}
}
