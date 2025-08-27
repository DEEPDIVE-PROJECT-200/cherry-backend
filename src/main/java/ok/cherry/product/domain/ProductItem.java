package ok.cherry.product.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ok.cherry.product.domain.status.ProductItemStatus;
import ok.cherry.product.domain.type.ProductCondition;

@Entity
@Access(AccessType.FIELD)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long productId;

	private String serialNumber;

	@Enumerated(EnumType.STRING)
	private ProductCondition condition;

	@Enumerated(EnumType.STRING)
	private ProductItemStatus status;

	private LocalDateTime registeredAt;

	public static ProductItem create(Long productId, String serialNumber, ProductCondition condition) {
		ProductItem item = new ProductItem();
		item.setProductId(productId);
		item.setSerialNumber(serialNumber);
		item.setCondition(condition);
		item.status = ProductItemStatus.AVAILABLE;
		item.registeredAt = LocalDateTime.now();
		return item;
	}

	public void rent() {
		verifyAvailableForRent();
		this.status = ProductItemStatus.RENTED;
	}

	public void returnItem() {
		verifyRentedForReturn();
		this.status = ProductItemStatus.AVAILABLE;
	}

	public void changeCondition(ProductCondition condition) {
		setCondition(condition);
	}

	private void setProductId(Long productId) {
		if (productId == null) {
			throw new IllegalArgumentException("no product id");
		}
		this.productId = productId;
	}

	private void setSerialNumber(String serialNumber) {
		if (serialNumber == null || serialNumber.trim().isEmpty()) {
			throw new IllegalArgumentException("no serial number");
		}
		this.serialNumber = serialNumber;
	}

	private void setCondition(ProductCondition condition) {
		if (condition == null) {
			throw new IllegalArgumentException("no condition");
		}
		this.condition = condition;
	}

	private void verifyAvailableForRent() {
		if (status != ProductItemStatus.AVAILABLE) {
			throw new IllegalStateException("product item not available");
		}
	}

	private void verifyRentedForReturn() {
		if (status != ProductItemStatus.RENTED) {
			throw new IllegalStateException("product item not rented");
		}
	}
}
