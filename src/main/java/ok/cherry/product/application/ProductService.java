package ok.cherry.product.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.dao.DataIntegrityViolationException;
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
import ok.cherry.product.exception.ProductError;
import ok.cherry.product.infrastructure.ProductRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	@Transactional
	public ProductCreateResponse createProduct(ProductCreateRequest request) {
		BigDecimal dailyRentalPrice = BigDecimal.valueOf(request.dailyRentalPrice());
		LocalDate launchedAt = LocalDate.parse(request.launchedAt());

		List<ProductThumbnailDetail> thumbnailDetails = IntStream.range(0, request.thumbnailImages().size())
			.mapToObj(i -> ProductThumbnailDetail.create(request.thumbnailImages().get(i), i))
			.toList();

		List<ProductImageDetail> imageDetails = IntStream.range(0, request.detailImages().size())
			.mapToObj(i -> ProductImageDetail.create(request.detailImages().get(i), i))
			.toList();

		Product product = Product.create(
			request.name(),
			request.brand(),
			request.colors(),
			dailyRentalPrice,
			launchedAt,
			thumbnailDetails,
			imageDetails
		);

		try{
			Product savedProduct = productRepository.save(product);
			return ProductCreateResponse.of(savedProduct.getId());
		} catch (DataIntegrityViolationException e) {
			throw new BusinessException(ProductError.DUPLICATE_PRODUCT);
		}
	}
}
