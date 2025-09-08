package ok.cherry.product.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.global.s3.S3Service;
import ok.cherry.product.domain.Product;
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

		List<String> imagePrefix = new ArrayList<>();
		product.getDetail().getProductThumbnailDetails()
			.forEach(detail -> imagePrefix.add(detail.getImageUrl()));

		product.getDetail().getProductImageDetails()
			.forEach(detail -> imagePrefix.add(detail.getImageUrl()));

		if (!imagePrefix.isEmpty()) {
			s3Service.deleteFiles(imagePrefix);
		}

		productRepository.delete(product);
	}
}
