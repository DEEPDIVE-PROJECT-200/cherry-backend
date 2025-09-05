package ok.cherry.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ok.cherry.global.exception.error.DomainException;
import ok.cherry.product.exception.ProductError;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductThumbnailDetail {

	@Column(nullable = false)
	private String imageUrl;

	@Column(nullable = false)
	private Integer displayOrder;

	public static ProductThumbnailDetail create(String imageUrl, Integer displayOrder) {
		ProductThumbnailDetail detail = new ProductThumbnailDetail();
		detail.setImageUrl(imageUrl);
		detail.setDisplayOrder(displayOrder);
		return detail;
	}

	public void changeImageUrl(String imageUrl) {
		setImageUrl(imageUrl);
	}

	public void changeDisplayOrder(Integer displayOrder) {
		setDisplayOrder(displayOrder);
	}

	private void setImageUrl(String imageUrl) {
		if (imageUrl == null || imageUrl.trim().isEmpty()) {
			throw new DomainException(ProductError.IMAGE_URL_IS_NOT_EMPTY);
		}
		this.imageUrl = imageUrl;
	}

	private void setDisplayOrder(Integer displayOrder) {
		if (displayOrder == null) {
			throw new DomainException(ProductError.DISPLAY_ORDER_IS_NOT_EMPTY);
		} else if (displayOrder < 0) {
			throw new DomainException(ProductError.INVALID_DISPLAY_ORDER);
		}
		this.displayOrder = displayOrder;
	}
}
