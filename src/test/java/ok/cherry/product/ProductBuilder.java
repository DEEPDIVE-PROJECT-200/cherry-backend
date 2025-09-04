package ok.cherry.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.type.Brand;
import ok.cherry.product.domain.type.Color;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductBuilder {

	public String name = "SONY WH-1000XM6";
	public Brand brand = Brand.SONY;
	public List<Color> colors = List.of(Color.BLACK, Color.WHITE);
	public BigDecimal dailyRentalPrice = BigDecimal.valueOf(10000);
	public LocalDate launchedAt = LocalDate.of(2025, 5, 16);

	public static Product create() {
		return builder().build();
	}

	public static ProductBuilder builder() {
		return new ProductBuilder();
	}

	public Product build() {
		return Product.create(name, brand, colors, dailyRentalPrice, launchedAt, null, null);
	}

	public ProductBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public ProductBuilder withBrand(Brand brand) {
		this.brand = brand;
		return this;
	}

	public ProductBuilder withColors(List<Color> colors) {
		this.colors = colors;
		return this;
	}

	public ProductBuilder withDailyRentalPrice(BigDecimal dailyRentalPrice) {
		this.dailyRentalPrice = dailyRentalPrice;
		return this;
	}

	public ProductBuilder withLaunchedAt(LocalDate launchedAt) {
		this.launchedAt = launchedAt;
		return this;
	}
}
