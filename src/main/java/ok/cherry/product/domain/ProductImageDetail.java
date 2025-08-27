package ok.cherry.product.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImageDetail {

	private String imageUrl;

	private Integer displayOrder;

	public static ProductImageDetail create(String imageUrl, Integer displayOrder) {
		ProductImageDetail detail = new ProductImageDetail();
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
			throw new IllegalArgumentException("image url");
		}
		this.imageUrl = imageUrl;
	}

	private void setDisplayOrder(Integer displayOrder) {
		if (displayOrder == null || displayOrder < 0) {
			throw new IllegalArgumentException("display order");
		}
		this.displayOrder = displayOrder;
	}
}
