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
class ProductCreateServiceTest {

	@Autowired
	ProductCreateService productCreateService;

	@Autowired
	ProductRepository productRepository;

	@Test
	@DisplayName("상품 등록에 성공한다")
	void createProduct_success() {
		// given
		ProductCreateRequest request = new ProductCreateRequest(
			"WH-1000XM5",
			Brand.SONY,
			List.of(Color.BLACK, Color.MIDNIGHT_BLUE),
			5000L,
			"2023-01-15",
			List.of("thumbnail1.jpg", "thumbnail2.jpg"),
			List.of("detail1.jpg", "detail2.jpg")
		);

		// when
		ProductCreateResponse response = productCreateService.createProduct(request);

		// then
		assertThat(response).isNotNull();

		Product savedProduct = productRepository.findById(response.productId()).orElseThrow();

		assertThat(savedProduct.getName()).isEqualTo("WH-1000XM5");
		assertThat(savedProduct.getBrand()).isEqualTo(Brand.SONY);
		assertThat(savedProduct.getColors()).hasSize(2)
			.containsExactly(Color.BLACK, Color.MIDNIGHT_BLUE);
		assertThat(savedProduct.getDailyRentalPrice()).isEqualTo(BigDecimal.valueOf(5000L));
		assertThat(savedProduct.getLaunchedAt()).isEqualTo(LocalDate.parse("2023-01-15"));

		List<ProductThumbnailDetail> thumbnailDetails = savedProduct.getDetail().getProductThumbnailDetails();
		assertThat(thumbnailDetails).hasSize(2);
		assertThat(thumbnailDetails.get(0).getImageUrl()).isEqualTo("thumbnail1.jpg");
		assertThat(thumbnailDetails.get(1).getImageUrl()).isEqualTo("thumbnail2.jpg");

		List<ProductImageDetail> imageDetails = savedProduct.getDetail().getProductImageDetails();
		assertThat(imageDetails).hasSize(2);
		assertThat(imageDetails.get(0).getImageUrl()).isEqualTo("detail1.jpg");
		assertThat(imageDetails.get(1).getImageUrl()).isEqualTo("detail2.jpg");
	}

	@Test
	@DisplayName("중복된 상품명으로 상품 등록 시 예외가 발생한다.")
	public void createProduct_fail_duplicate_name() {
		// given
		ProductCreateRequest request = new ProductCreateRequest(
			"WH-1000XM5",
			Brand.SONY,
			List.of(Color.BLACK, Color.MIDNIGHT_BLUE),
			5000L,
			"2023-01-15",
			List.of("thumbnail1.jpg", "thumbnail2.jpg"),
			List.of("detail1.jpg","detail2.jpg")
		);

		ProductCreateResponse response = productCreateService.createProduct(request);

		ProductCreateRequest duplicateRequest = new ProductCreateRequest(
			"WH-1000XM5",
			Brand.SONY,
			List.of(Color.BLACK, Color.MIDNIGHT_BLUE),
			5000L,
			"2023-01-15",
			List.of("thumbnail1.jpg", "thumbnail2.jpg"),
			List.of("detail1.jpg", "detail2.jpg")
		);

		// when & then
		assertThatThrownBy(() -> productCreateService.createProduct(duplicateRequest))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ProductError.DUPLICATE_PRODUCT.getMessage());
	}
}