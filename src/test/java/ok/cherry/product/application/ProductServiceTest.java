package ok.cherry.product.application;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.product.application.dto.request.ProductCreateRequest;
import ok.cherry.product.application.dto.response.ProductCreateResponse;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.ProductImageDetail;
import ok.cherry.product.domain.ProductThumbnailDetail;
import ok.cherry.product.domain.type.Brand;
import ok.cherry.product.domain.type.Color;
import ok.cherry.product.exception.ProductError;
import ok.cherry.product.infrastructure.ProductRepository;

@SpringBootTest
@Transactional
class ProductServiceTest {

	@Autowired
	ProductService productService;

	@Autowired
	ProductRepository productRepository;

	@Test
	@DisplayName("상품 등록에 성공한다")
	void createProduct_success() {
		// given
		ProductCreateRequest request = new ProductCreateRequest(
			"WH-1000XM5",
			"SONY",
			List.of("BLACK", "MIDNIGHT_BLUE"),
			5000L,
			"2023-01-15",
			List.of("product/thumbnails/thumbnail1.jpg", "product/thumbnails/thumbnail2.jpg"),
			List.of("product/details/detail1.jpg", "product/details/detail2.jpg")
		);

		// when
		ProductCreateResponse response = productService.createProduct(request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.productId()).isEqualTo(1L);

		Product savedProduct = productRepository.findById(response.productId()).orElseThrow();

		assertThat(savedProduct.getName()).isEqualTo("WH-1000XM5");
		assertThat(savedProduct.getBrand()).isEqualTo(Brand.SONY);
		assertThat(savedProduct.getColors()).hasSize(2)
			.containsExactly(Color.BLACK, Color.MIDNIGHT_BLUE);
		assertThat(savedProduct.getDailyRentalPrice()).isEqualTo(BigDecimal.valueOf(5000L));
		assertThat(savedProduct.getLaunchedAt()).isEqualTo(LocalDate.parse("2023-01-15"));

		List<ProductThumbnailDetail> thumbnailDetails = savedProduct.getDetail().getProductThumbnailDetails();
		assertThat(thumbnailDetails).hasSize(2);
		assertThat(thumbnailDetails.get(0).getImageUrl()).isEqualTo("product/thumbnails/thumbnail1.jpg");
		assertThat(thumbnailDetails.get(1).getImageUrl()).isEqualTo("product/thumbnails/thumbnail2.jpg");

		List<ProductImageDetail> imageDetails = savedProduct.getDetail().getProductImageDetails();
		assertThat(imageDetails).hasSize(2);
		assertThat(imageDetails.get(0).getImageUrl()).isEqualTo("product/details/detail1.jpg");
		assertThat(imageDetails.get(1).getImageUrl()).isEqualTo("product/details/detail2.jpg");
	}

	@Test
	@DisplayName("지원하지 않는 브랜드 입력 시 예외가 발생한다")
	void createProduct_fail_invalidBrand() {
		// given
		String invalidBrand = "INVALID_BRAND";
		ProductCreateRequest request = new ProductCreateRequest(
			"WH-1000XM5",
			invalidBrand,
			List.of("BLACK", "MIDNIGHT_BLUE"),
			5000L,
			"2023-01-15",
			List.of("product/thumbnails/thumbnail1.jpg", "product/thumbnails/thumbnail2.jpg"),
			List.of("product/details/detail1.jpg", "product/details/detail2.jpg")
		);

		// when & then
		assertThatThrownBy(() -> productService.createProduct(request))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ProductError.INVALID_BRAND.getMessage());
	}

	@Test
	@DisplayName("지원하지 않는 색상 옵션 입력 시 예외가 발생한다")
	void createProduct_fail_invalidColor() {
		// given
		String invalidColor = "INVALID_COLOR";
		ProductCreateRequest request = new ProductCreateRequest(
			"WH-1000XM5",
			"SONY",
			List.of(invalidColor),
			5000L,
			"2023-01-15",
			List.of("product/thumbnails/thumbnail1.jpg", "product/thumbnails/thumbnail2.jpg"),
			List.of("product/details/detail1.jpg", "product/details/detail2.jpg")
		);

		// when & then
		assertThatThrownBy(() -> productService.createProduct(request))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ProductError.INVALID_COLOR.getMessage());
	}
}