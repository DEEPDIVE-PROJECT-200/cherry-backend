package ok.cherry.rental;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ok.cherry.product.ProductBuilder;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.type.Color;
import ok.cherry.rental.domain.RentalItem;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalItemBuilder {

	public Product product = ProductBuilder.create();
	public BigDecimal price = BigDecimal.valueOf(10000);
	public Color color = Color.BLACK;

	public static RentalItem create() {
		return builder().build();
	}

	public static RentalItemBuilder builder() {
		return new RentalItemBuilder();
	}

	public RentalItem build() {
		return RentalItem.create(product, price, color);
	}

	public RentalItemBuilder withProduct(Product product) {
		this.product = product;
		return this;
	}

	public RentalItemBuilder withPrice(BigDecimal price) {
		this.price = price;
		return this;
	}

	public RentalItemBuilder withColor(Color color) {
		this.color = color;
		return this;
	}
}
