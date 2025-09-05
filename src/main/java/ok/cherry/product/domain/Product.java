package ok.cherry.product.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
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

	@Column(nullable = false, unique = true)
	private String name;

	@Column(name = "brand", nullable = false)
	@Enumerated(EnumType.STRING)
	private Brand brand;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private List<Color> colors = new ArrayList<>();

	@Column(nullable = false)
	private BigDecimal dailyRentalPrice;

	@Column(nullable = false)
	private LocalDate launchedAt;

	@Embedded
	private ProductDetail detail;

	public static Product create(
		String name,
		Brand brand,
		List<Color> colors,
		BigDecimal dailyRentalPrice,
		LocalDate launchedAt,
		List<ProductThumbnailDetail> thumbnailDetails,
		List<ProductImageDetail> imageDetails
	) {
		Product product = new Product();
		product.name = name;
		product.brand = brand;
		product.dailyRentalPrice = dailyRentalPrice;
		product.launchedAt = launchedAt;
		product.detail = ProductDetail.create();
		product.detail.changeProductThumbnailDetails(thumbnailDetails);
		product.detail.changeProductImageDetails(imageDetails);

		if (colors != null) {
			product.colors.clear();
			product.colors.addAll(colors);
		}

		return product;
	}
}
