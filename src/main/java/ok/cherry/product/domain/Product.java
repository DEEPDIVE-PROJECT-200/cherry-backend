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
import ok.cherry.product.domain.type.Brand;
import ok.cherry.product.domain.type.Color;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(name = "brand", nullable = false)
	@Enumerated(EnumType.STRING)
	private Brand brand;

	@Column(nullable = false)
	private String model;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private List<Color> colors = new ArrayList<>();

	@Column(nullable = false)
	private BigDecimal dailyRentalPrice;

	@Column(nullable = false)
	private LocalDateTime launchedAt;

	@Embedded
	private ProductDetail detail;

	public static Product create(
		String name,
		Brand brand,
		String model,
		List<Color> colors,
		BigDecimal dailyRentalPrice,
		LocalDateTime launchedAt,
		List<ProductThumbNailDetail> thumbNailDetails,
		List<ProductImageDetail> imageDetails
		) {
		Product product = new Product();
		product.name = name;
		product.brand = brand;
		product.model = model;
		product.dailyRentalPrice = dailyRentalPrice;
		product.launchedAt = launchedAt;
		product.detail = ProductDetail.create();
		product.detail.changeProductThumbNailDetails(thumbNailDetails);
		product.detail.changeProductImageDetails(imageDetails);

		if(colors != null) {
			product.colors.clear();
			product.colors.addAll(colors);
		}

		return product;
	}
}
