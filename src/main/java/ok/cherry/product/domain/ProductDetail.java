package ok.cherry.product.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDetail {

	@Column(nullable = false)
	private LocalDateTime registeredAt;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(
		name = "product_thumbnail_details",
		joinColumns = @JoinColumn(name = "product_id", nullable = false)
	)
	@OrderColumn(name = "thumbnail_idx")
	private List<ProductThumbnailDetail> productThumbnailDetails = new ArrayList<>();

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(
		name = "product_image_details",
		joinColumns = @JoinColumn(name = "product_id", nullable = false)
	)
	@OrderColumn(name = "image_idx")
	private List<ProductImageDetail> productImageDetails = new ArrayList<>();

	static ProductDetail create() {
		ProductDetail detail = new ProductDetail();
		detail.registeredAt = LocalDateTime.now();
		return detail;
	}

	void changeProductThumbNailDetails(List<ProductThumbnailDetail> details) {
		this.productThumbnailDetails.clear();
		if (details != null) {
			this.productThumbnailDetails.addAll(details);
		}
	}

	void changeProductImageDetails(List<ProductImageDetail> details) {
		this.productImageDetails.clear();
		if (details != null) {
			this.productImageDetails.addAll(details);
		}
	}
}
