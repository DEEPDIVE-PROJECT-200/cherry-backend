package ok.cherry.product.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ok.cherry.global.exception.error.DomainException;
import ok.cherry.product.exception.ProductError;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDetail {

	private LocalDateTime registeredAt;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "product_thumbnail_details", joinColumns = @JoinColumn(name = "product_id"))
	@OrderColumn(name = "thumbnail_idx")
	private List<ProductThumbNailDetail> productThumbNailDetails = new ArrayList<>();

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "product_image_details", joinColumns = @JoinColumn(name = "product_id"))
	@OrderColumn(name = "image_idx")
	private List<ProductImageDetail> productImageDetails = new ArrayList<>();

	static ProductDetail create() {
		ProductDetail detail = new ProductDetail();
		detail.registeredAt = LocalDateTime.now();
		return detail;
	}

	void changeProductThumbNailDetails(List<ProductThumbNailDetail> details) {
		this.productThumbNailDetails.clear();
		if (details != null) {
			this.productThumbNailDetails.addAll(details);
		}
	}

	void changeProductImageDetails(List<ProductImageDetail> details) {
		this.productImageDetails.clear();
		if (details != null) {
			this.productImageDetails.addAll(details);
		}
	}
}
