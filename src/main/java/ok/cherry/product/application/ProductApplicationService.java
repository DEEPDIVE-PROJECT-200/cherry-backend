package ok.cherry.product.application;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.global.s3.S3Service;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.ProductImageDetail;
import ok.cherry.product.domain.ProductThumbnailDetail;
import ok.cherry.product.exception.ProductError;
import ok.cherry.product.infrastructure.ProductRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductApplicationService {

	private final ProductRepository productRepository;
	private final S3Service s3Service;

	public void deleteProduct(Long productId) {
		Product product = productRepository.findByIdWithDetails(productId)
			.orElseThrow(() -> new BusinessException(ProductError.PRODUCT_NOT_FOUND));

		List<String> imagePrefix = Stream.concat(
			product.getDetail().getProductThumbnailDetails().stream()
				.map(ProductThumbnailDetail::getImageUrl),
			product.getDetail().getProductImageDetails().stream()
				.map(ProductImageDetail::getImageUrl)
		).toList();

		productRepository.delete(product);

		if (!imagePrefix.isEmpty()) {
			s3Service.deleteFiles(imagePrefix);
		}
	}
}
