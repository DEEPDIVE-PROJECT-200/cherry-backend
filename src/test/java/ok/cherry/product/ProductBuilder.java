package ok.cherry.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
	public String model = "WH-1000XM6";
	public List<Color> colors = List.of(Color.BLACK, Color.WHITE);
	public BigDecimal dailyRentalPrice = BigDecimal.valueOf(10000);
	public LocalDateTime launchedAt = LocalDateTime.of(2025, 5, 16, 0, 0, 0);

	public static Product create() {
		return builder().build();
	}

	public static ProductBuilder builder() {
		return new ProductBuilder();
	}

	public Product build() {
		return Product.create(name, brand, model, colors, dailyRentalPrice, launchedAt, null, null);
	}

	public ProductBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public ProductBuilder withBrand(Brand brand) {
		this.brand = brand;
		return this;
	}

	public ProductBuilder withModel(String model) {
		this.model = model;
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

	public ProductBuilder withLaunchedAt(LocalDateTime launchedAt) {
		this.launchedAt = launchedAt;
		return this;
	}
}
