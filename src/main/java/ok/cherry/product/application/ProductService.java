package ok.cherry.product.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	@Transactional
	public ProductCreateResponse createProduct(ProductCreateRequest request) {
		// 색상, 브랜드 Enum 타입 유효성 검증
		String requestedBrand = request.brand().toUpperCase();
		if (!isValidEnum(Brand.class, requestedBrand)) {
			throw new BusinessException(ProductError.INVALID_BRAND);
		}
		Brand brand = Brand.valueOf(requestedBrand);

		List<Color> colors = new ArrayList<>();
		for (String color : request.colors()) {
			String requestedColor = color.toUpperCase();
			if (!isValidEnum(Color.class, requestedColor)) {
				throw new BusinessException(ProductError.INVALID_COLOR);
			}
			colors.add(Color.valueOf(requestedColor));
		}

		BigDecimal dailyRentalPrice = BigDecimal.valueOf(request.dailyRentalPrice());
		LocalDate launchedAt = LocalDate.parse(request.launchedAt());

		List<ProductThumbnailDetail> thumbnailDetails = new ArrayList<>();
		for (int i = 0; i < request.thumbnailImages().size(); i++) {
			String imageUrl = request.thumbnailImages().get(i);
			ProductThumbnailDetail detail = ProductThumbnailDetail.create(imageUrl, i);
			thumbnailDetails.add(detail);
		}

		List<ProductImageDetail> imageDetails = new ArrayList<>();
		for (int i = 0; i < request.detailImages().size(); i++) {
			String imageUrl = request.detailImages().get(i);
			ProductImageDetail detail = ProductImageDetail.create(imageUrl, i);
			imageDetails.add(detail);
		}

		Product product = Product.create(
			request.name(),
			brand,
			colors,
			dailyRentalPrice,
			launchedAt,
			thumbnailDetails,
			imageDetails
		);

		Product savedProduct = productRepository.save(product);

		return ProductCreateResponse.of(savedProduct.getId());
	}

	private <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String value) {
		if (value == null) {
			return false;
		}

		try {
			Enum.valueOf(enumClass, value);
			return true;
		} catch (Exception e) {
			log.error("Enum 타입 검증 중 오류가 발생하였습니다. {}", e.getMessage(), e);
			return false;
		}
	}
}
