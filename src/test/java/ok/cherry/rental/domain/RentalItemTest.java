package ok.cherry.rental.domain;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ok.cherry.product.ProductBuilder;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.type.Color;
import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.RentalItemBuilder;
import ok.cherry.rental.domain.status.ReviewStatus;

class RentalItemTest {

	@Test
	@DisplayName("대여 아이템 생성 시 기본 리뷰 상태는 PENDING이다")
	void createRentalItemWithDefaultReviewStatus() {
		// given
		Product product = ProductBuilder.create();
		BigDecimal price = BigDecimal.valueOf(10000);
		Color color = Color.BLACK;

		// when
		RentalItem rentalItem = RentalItem.create(product, price, color);

		// then
		assertThat(rentalItem.getReviewStatus()).isEqualTo(ReviewStatus.PENDING);
	}

	@Test
	@DisplayName("대여 아이템에 대여를 설정할 수 있다")
	void setRentalToRentalItem() {
		// given
		RentalItem rentalItem = RentalItemBuilder.create();
		Rental rental = RentalBuilder.create();

		// when
		rentalItem.setRental(rental);

		// then
		assertThat(rentalItem.getRental()).isEqualTo(rental);
	}
}