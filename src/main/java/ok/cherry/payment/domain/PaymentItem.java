package ok.cherry.payment.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ok.cherry.product.domain.type.Brand;
import ok.cherry.product.domain.type.Color;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentItem {

	@Column(nullable = false)
	private String productName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Brand brand;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color color;

	@Column(nullable = false)
	private BigDecimal price;

	public static PaymentItem create(
		String productName,
		Brand brand,
		Color color,
		BigDecimal price
	) {
		PaymentItem item = new PaymentItem();
		item.productName = productName;
		item.brand = brand;
		item.color = color;
		item.price = price;
		return item;
	}
}
