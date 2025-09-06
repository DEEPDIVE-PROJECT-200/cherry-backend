package ok.cherry.product.infrastructure;

import static ok.cherry.product.domain.QProduct.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import ok.cherry.product.application.dto.request.ProductSortType;
import ok.cherry.product.application.dto.response.ProductThumbnailResponse;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.type.Brand;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public List<ProductThumbnailResponse> findProductsWithConditions(
		List<Brand> brands,
		ProductSortType sortType,
		Long lastProductId,
		int limit
	) {
		return queryFactory
			.select(Projections.constructor(ProductThumbnailResponse.class,
				product.id,
				product.name,
				product.brand,
				product.thumbnailUrl,
				product.dailyRentalPrice
			))
			.from(product)
			.where(
				inBrands(brands),
				getPaginationCondition(sortType, lastProductId)
			)
			.orderBy(getOrderSpecifier(sortType).toArray(OrderSpecifier[]::new))
			.limit(limit)
			.fetch();
	}

	private BooleanExpression inBrands(List<Brand> brands) {
		if (brands == null || brands.isEmpty()) {
			return null;
		}
		return product.brand.in(brands);
	}

	private BooleanExpression getPaginationCondition(ProductSortType sortType, Long lastProductId) {
		if (lastProductId == null) {
			return null;
		}

		Product lastProduct = findProductById(lastProductId);
		if (lastProduct == null) {
			return null;
		}

		return buildCondition(sortType, lastProduct);
	}

	private Product findProductById(Long productId) {
		return queryFactory.selectFrom(product)
			.where(product.id.eq(productId))
			.fetchOne();
	}

	private BooleanExpression buildCondition(ProductSortType sortType, Product lastProduct) {
		return switch (sortType) {
			case LAUNCHED -> product.launchedAt.lt(lastProduct.getLaunchedAt())
				.or(product.launchedAt.eq(lastProduct.getLaunchedAt())
					.and(product.id.lt(lastProduct.getId())));
			case PRICE_ASC -> product.dailyRentalPrice.gt(lastProduct.getDailyRentalPrice())
				.or(product.dailyRentalPrice.eq(lastProduct.getDailyRentalPrice())
					.and(product.id.lt(lastProduct.getId())));
			case PRICE_DESC -> product.dailyRentalPrice.lt(lastProduct.getDailyRentalPrice())
				.or(product.dailyRentalPrice.eq(lastProduct.getDailyRentalPrice())
					.and(product.id.lt(lastProduct.getId())));
			default -> product.detail.registeredAt.lt(lastProduct.getDetail().getRegisteredAt())
				.or(product.detail.registeredAt.eq(lastProduct.getDetail().getRegisteredAt())
					.and(product.id.lt(lastProduct.getId())));
		};
	}

	private List<OrderSpecifier<?>> getOrderSpecifier(ProductSortType sortType) {
		return switch (sortType) {
			case LAUNCHED -> List.of(
				new OrderSpecifier<>(Order.DESC, product.launchedAt),
				new OrderSpecifier<>(Order.DESC, product.id)
			);
			case PRICE_ASC -> List.of(
				new OrderSpecifier<>(Order.ASC, product.dailyRentalPrice),
				new OrderSpecifier<>(Order.DESC, product.id)
			);
			case PRICE_DESC -> List.of(
				new OrderSpecifier<>(Order.DESC, product.dailyRentalPrice),
				new OrderSpecifier<>(Order.DESC, product.id)
			);
			default -> List.of(
				new OrderSpecifier<>(Order.DESC, product.detail.registeredAt),
				new OrderSpecifier<>(Order.DESC, product.id)
			);
		};
	}
}