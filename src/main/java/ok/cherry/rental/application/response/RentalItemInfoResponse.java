package ok.cherry.rental.application.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import ok.cherry.product.domain.type.Color;
import ok.cherry.rental.domain.RentalItem;

@Schema(description = "대여 상품 정보 DTO")
public record RentalItemInfoResponse(

	@Schema(description = "상품명", example = "WH-1000XM5")
	String productName,

	@Schema(description = "상품 색상 옵션", example = "WHITE")
	Color color,

	@Schema(description = "일일 대여 가격(원)", example = "10000.00")
	BigDecimal price,

	@Schema(description = "상품 썸네일 URL", example = "21c559ed-e104-44c0-a027-8db0a3036457_01.jpg")
	String productThumbnailUrl
) {
	public static RentalItemInfoResponse from(RentalItem rentalItem) {
		return new RentalItemInfoResponse(
			rentalItem.getProduct().getName(),
			rentalItem.getColor(),
			rentalItem.getPrice(),
			rentalItem.getProduct().getThumbnailUrl()
		);
	}
}
