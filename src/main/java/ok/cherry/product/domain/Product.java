package ok.cherry.product.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ok.cherry.global.value.Brand;
import ok.cherry.global.value.Color;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(name = "brand")
	@Enumerated(EnumType.STRING)
	private Brand brand;

	private String model;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	private List<Color> colors = new ArrayList<>();

	private BigDecimal dailyRentalPrice;

	private LocalDateTime launchedAt;

	@Embedded
	private ProductDetail detail;

	public static Product create(String name, Brand brand, String model, List<Color> colors, BigDecimal dailyRentalPrice, LocalDateTime launchedAt) {
		Product product = new Product();
		product.name = name;
		product.brand = brand;
		product.model = model;
		product.dailyRentalPrice = dailyRentalPrice;
		product.launchedAt = launchedAt;
		product.detail = ProductDetail.create();

		if(colors != null) {
			product.colors.clear();
			product.colors.addAll(colors);
		}

		return product;
	}

	public void changeProductThumbNailDetails(List<ProductThumbNailDetail> details) {
		this.detail.changeProductThumbNailDetails(details);
	}

	public void changeProductImageDetails(List<ProductImageDetail> details) {
		this.detail.changeProductImageDetails(details);
	}
}
