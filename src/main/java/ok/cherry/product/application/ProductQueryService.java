package ok.cherry.product.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ok.cherry.product.application.dto.request.ProductSortType;
import ok.cherry.product.application.dto.response.ProductSearchResponse;
import ok.cherry.product.application.dto.response.ProductThumbnailResponse;
import ok.cherry.product.domain.type.Brand;
import ok.cherry.product.infrastructure.ProductRepositoryCustom;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

	private final ProductRepositoryCustom productRepositoryCustom;

	public ProductSearchResponse searchProductsWithConditions(
		List<Brand> brands,
		ProductSortType sortType,
		Long lastProductId,
		int limit
	) {
		List<ProductThumbnailResponse> products =
			productRepositoryCustom.findProductsWithConditions(brands, sortType, lastProductId, limit + 1);

		boolean hasNext = products.size() > limit;
		if (hasNext) {
			products.remove(limit);
		}

		Long lastId = null;
		if (!products.isEmpty()) {
			lastId = products.getLast().id();
		}

		return ProductSearchResponse.of(products, hasNext, lastId);
	}
}
