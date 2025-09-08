package ok.cherry.rental.application.response;

import java.math.BigDecimal;

import ok.cherry.product.domain.type.Color;
import ok.cherry.rental.domain.RentalItem;

public record RentalItemInfoResponse(
	String productName,
	Color color,
	BigDecimal price,
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
