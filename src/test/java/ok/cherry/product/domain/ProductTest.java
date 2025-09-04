package ok.cherry.product.domain;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ok.cherry.product.domain.type.Brand;
import ok.cherry.product.domain.type.Color;

class ProductTest {

	@Test
	@DisplayName("상품 등록 시 등록일자가 설정된다")
	void createProductWithDetail() {
		// given
		String name = "SONY WH-1000XM6";
		Brand brand = Brand.SONY;
		String model = "WH-1000XM6";
		List<Color> colors = List.of(Color.BLACK, Color.WHITE);
		BigDecimal dailyRentalPrice = new BigDecimal(10000);
		LocalDateTime launchedAt = LocalDateTime.of(2025, 5, 16, 0, 0, 0);

		// when
		Product product = Product.create(name, brand, model, colors, dailyRentalPrice, launchedAt, null, null);

		// then
		assertThat(product.getDetail().getRegisteredAt()).isNotNull();
	}
}