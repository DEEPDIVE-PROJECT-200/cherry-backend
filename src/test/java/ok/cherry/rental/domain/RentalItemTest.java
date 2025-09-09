package ok.cherry.rental.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.RentalItemBuilder;

class RentalItemTest {

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