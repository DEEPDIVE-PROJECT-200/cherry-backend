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

import jakarta.persistence.EntityManager;
import ok.cherry.product.ProductBuilder;
import ok.cherry.product.application.dto.request.ProductSortType;
import ok.cherry.product.application.dto.response.ProductSearchResponse;
import ok.cherry.product.domain.type.Brand;
import ok.cherry.product.infrastructure.ProductRepository;

@SpringBootTest
@Transactional
class ProductQueryServiceTest {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductQueryService productQueryService;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("브랜드로 필터링하여 상품을 조회한다")
	void searchProductsWithConditions_filterByBrand() {
		// given
		saveProducts();
		List<Brand> brands = List.of(Brand.SONY, Brand.APPLE);

		// when
		ProductSearchResponse results = productQueryService.searchProductsWithConditions(
			brands,
			ProductSortType.LAUNCHED,
			null,
			3
		);

		// then
		assertThat(results.products()).hasSize(2);
		assertThat(results.products()).extracting("brand")
			.containsOnly(Brand.SONY, Brand.APPLE);
	}

	@Test
	@DisplayName("최신 출시일 순으로 상품을 정렬하여 조회한다")
	void searchProductsWithConditions_sortByLaunched() {
		// given
		saveProducts();

		// when
		ProductSearchResponse results = productQueryService.searchProductsWithConditions(
			null,
			ProductSortType.LAUNCHED,
			null,
			5
		);

		// then
		assertThat(results.products()).hasSize(5);
		assertThat(results.products()).extracting("name")
			.containsExactly(
				"Nothing Headphone",
				"Sony WH-1000XM6",
				"Marshall Monitor III A.N.C.",
				"Dyson OnTrac",
				"Bose QuietComfort Ultra Headphones"
			);
	}

	@Test
	@DisplayName("낮은 가격 순으로 상품을 정렬하여 조회한다")
	void searchProductsWithConditions_sortByPriceAsc() {
		// given
		saveProducts();

		// when
		ProductSearchResponse results = productQueryService.searchProductsWithConditions(
			null,
			ProductSortType.PRICE_ASC,
			null,
			5
		);

		// then
		assertThat(results.products()).hasSize(5);
		assertThat(results.products()).extracting("name")
			.containsExactly(
				"JBL Tour One M2",
				"Nothing Headphone",
				"Marshall Monitor III A.N.C.",
				"Bose QuietComfort Ultra Headphones",
				"Sennheiser MOMENTUM 4 Wireless"
			);
	}

	@Test
	@DisplayName("높은 가격 순으로 상품을 정렬하여 조회한다")
	void searchProductsWithConditions_sortByPriceDesc() {
		// given
		saveProducts();

		// when
		ProductSearchResponse results = productQueryService.searchProductsWithConditions(
			null,
			ProductSortType.PRICE_DESC,
			null,
			5
		);

		// then
		assertThat(results.products()).hasSize(5);
		assertThat(results.products()).extracting("name")
			.containsExactly(
				"Bowers & Wilkins Px8",
				"Bang & Olufsen Beoplay HX",
				"Apple AirPods Max",
				"Dyson OnTrac",
				"Sony WH-1000XM6"
			);
	}

	@Test
	@DisplayName("limit 수만큼 상품을 조회한다")
	void searchProductsWithConditions_withLimit() {
		// given
		saveProducts();
		int limit = 5;

		// when
		ProductSearchResponse results = productQueryService.searchProductsWithConditions(
			null,
			ProductSortType.REGISTERED,
			null,
			limit
		);

		// then
		assertThat(results.products()).hasSize(limit);
	}

	@Test
	@DisplayName("최신 출시일 순으로 페이지네이션이 올바르게 동작한다")
	void searchProductsWithConditions_paginationWithSortByLaunched() {
		// given
		saveProducts();
		ProductSearchResponse firstPage = productQueryService.searchProductsWithConditions(
			null,
			ProductSortType.LAUNCHED,
			null,
			2
		);
		Long lastProductId = firstPage.products().getLast().id();

		// when
		ProductSearchResponse secondPage = productQueryService.searchProductsWithConditions(
			null,
			ProductSortType.LAUNCHED,
			lastProductId,
			2
		);

		assertThat(secondPage.products()).hasSize(2);
		assertThat(secondPage.products()).extracting("name")
			.containsExactly("Marshall Monitor III A.N.C.", "Dyson OnTrac");
	}

	@Test
	@DisplayName("낮은 가격 순으로 페이지네이션이 올바르게 동작한다")
	void searchProductsWithConditions_paginationWithSortByPriceAsc() {
		// given
		saveProducts();
		ProductSearchResponse firstPage = productQueryService.searchProductsWithConditions(
			null,
			ProductSortType.PRICE_ASC,
			null,
			2
		);
		Long lastProductId = firstPage.products().getLast().id();

		// when
		ProductSearchResponse secondPage = productQueryService.searchProductsWithConditions(
			null,
			ProductSortType.PRICE_ASC,
			lastProductId,
			2
		);

		assertThat(secondPage.products()).hasSize(2);
		assertThat(secondPage.products()).extracting("name")
			.containsExactly("Marshall Monitor III A.N.C.", "Bose QuietComfort Ultra Headphones");
	}

	private void saveProducts() {
		productRepository.saveAll(List.of(
			ProductBuilder.builder()
				.withName("Sony WH-1000XM6")
				.withBrand(Brand.SONY)
				.withDailyRentalPrice(BigDecimal.valueOf(3_095))
				.withLaunchedAt(LocalDate.of(2025, 6, 18))
				.build(),
			ProductBuilder.builder()
				.withName("Apple AirPods Max")
				.withBrand(Brand.APPLE)
				.withDailyRentalPrice(BigDecimal.valueOf(3_845))
				.withLaunchedAt(LocalDate.of(2020, 12, 15))
				.build(),
			ProductBuilder.builder()
				.withName("Bose QuietComfort Ultra Headphones")
				.withBrand(Brand.BOSE)
				.withDailyRentalPrice(BigDecimal.valueOf(2_495))
				.withLaunchedAt(LocalDate.of(2023, 10, 16))
				.build(),
			ProductBuilder.builder()
				.withName("Sennheiser MOMENTUM 4 Wireless")
				.withBrand(Brand.SENNHEISER)
				.withDailyRentalPrice(BigDecimal.valueOf(2_595))
				.withLaunchedAt(LocalDate.of(2022, 8, 23))
				.build(),
			ProductBuilder.builder()
				.withName("Bang & Olufsen Beoplay HX")
				.withBrand(Brand.BANG_OLUFSEN)
				.withDailyRentalPrice(BigDecimal.valueOf(3_995))
				.withLaunchedAt(LocalDate.of(2021, 4, 6))
				.build(),
			ProductBuilder.builder()
				.withName("Bowers & Wilkins Px8")
				.withBrand(Brand.BOWERS_WILKINS)
				.withDailyRentalPrice(BigDecimal.valueOf(4_400))
				.withLaunchedAt(LocalDate.of(2022, 10, 11))
				.build(),
			ProductBuilder.builder()
				.withName("Marshall Monitor III A.N.C.")
				.withBrand(Brand.MARSHALL)
				.withDailyRentalPrice(BigDecimal.valueOf(2_445))
				.withLaunchedAt(LocalDate.of(2024, 11, 14))
				.build(),
			ProductBuilder.builder()
				.withName("Dyson OnTrac")
				.withBrand(Brand.DYSON)
				.withDailyRentalPrice(BigDecimal.valueOf(3_495))
				.withLaunchedAt(LocalDate.of(2024, 9, 3))
				.build(),
			ProductBuilder.builder()
				.withName("JBL Tour One M2")
				.withBrand(Brand.JBL)
				.withDailyRentalPrice(BigDecimal.valueOf(1_495))
				.withLaunchedAt(LocalDate.of(2023, 6, 29))
				.build(),
			ProductBuilder.builder()
				.withName("Nothing Headphone")
				.withBrand(Brand.NOTHING)
				.withDailyRentalPrice(BigDecimal.valueOf(1_995))
				.withLaunchedAt(LocalDate.of(2025, 7, 15))
				.build()
		));
		em.flush();
		em.clear();
	}
}