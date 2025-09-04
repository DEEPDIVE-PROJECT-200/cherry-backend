package ok.cherry.rental.domain;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.RentalItemBuilder;

class RentalTest {

	@Test
	@DisplayName("대여 생성 시 생성일자가 설정된다")
	void createRentalWithDetail() {
		// given
		Member member = MemberBuilder.create();
		RentalItem rentalItem = RentalItemBuilder.create();
		String rentalNumber = "CH-25090213363012345678";
		LocalDateTime startAt = LocalDateTime.now();
		LocalDateTime endAt = LocalDateTime.now().plusDays(7);

		// when
		Rental rental = Rental.create(member, List.of(rentalItem), rentalNumber, startAt, endAt);

		// then
		assertThat(rental.getDetail().getCreatedAt()).isNotNull();
	}

	@Test
	@DisplayName("대여 총 금액은 대여 아이템들의 비용 합계로 계산된다")
	void calculateTotalPriceOfRentalItems() {
		// given
		List<RentalItem> RentalItems = List.of(
			RentalItemBuilder.builder().withPrice(BigDecimal.valueOf(5000)).build(),
			RentalItemBuilder.builder().withPrice(BigDecimal.valueOf(10000)).build()
		);

		// when
		Rental rental = RentalBuilder.builder().withRentalItems(RentalItems).build();

		// then
		assertThat(rental.getTotalPrice()).isEqualTo(BigDecimal.valueOf(15000));
	}

	@Test
	@DisplayName("대여 생성 시 대여 아이템들과 대여의 연관관계가 설정된다")
	void setRentalItemAssociations() {
		// given
		List<RentalItem> RentalItems = List.of(
			RentalItemBuilder.builder().withPrice(BigDecimal.valueOf(10000)).build(),
			RentalItemBuilder.builder().withPrice(BigDecimal.valueOf(10000)).build()
		);

		// when
		Rental rental = RentalBuilder.builder().withRentalItems(RentalItems).build();

		// then
		assertThat(rental.getRentalItems()).hasSize(2);
		assertThat(rental.getRentalItems())
			.allMatch(rentalItem -> rentalItem.getRental() == rental);
	}
}