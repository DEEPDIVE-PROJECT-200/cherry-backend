package ok.cherry.rental.domain;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ok.cherry.global.exception.error.DomainException;
import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.RentalItemBuilder;
import ok.cherry.rental.domain.status.RentalStatus;
import ok.cherry.rental.domain.status.ReviewStatus;
import ok.cherry.rental.exception.RentalError;

class RentalTest {

	@Test
	@DisplayName("대여 생성 시 생성일자가 설정된다")
	void createRentalWithDetail() {
		// given
		Member member = MemberBuilder.create();
		RentalItem rentalItem = RentalItemBuilder.create();
		String rentalNumber = "CH-25090213363012345678";
		LocalDate startAt = LocalDate.now();
		LocalDate endAt = LocalDate.now().plusDays(7);

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

	@Test
	@DisplayName("대여 생성 시 대여번호가 형식이 다르면 예외가 발생한다")
	void createShippingWithTrackingNumber() {
		// given
		String wrongRentalNumber = "wrong_rental_number";

		// when & then
		assertThatThrownBy(() -> RentalBuilder.builder().withRentalNumber(wrongRentalNumber).build())
			.isInstanceOf(DomainException.class)
			.hasMessage(RentalError.INVALID_RENTAL_NUMBER.getMessage());
	}

	@Test
	@DisplayName("대여 활성화 시 대여 상태가 ACTIVE로 변경된다")
	void active_success() {
		// given
		Rental rental = RentalBuilder.create();

		// when
		rental.active();

		// then
		assertThat(rental.getRentalStatus()).isEqualTo(RentalStatus.ACTIVE);
	}

	@Test
	@DisplayName("대여 활성화 시 대여 상태가 PENDING이 아니면 예외가 발생한다")
	void active_notPending() {
		// given
		Rental rental = RentalBuilder.create();
		rental.active();

		// when & then
		assertThatThrownBy(() -> rental.active())
			.isInstanceOf(DomainException.class)
			.hasMessage(RentalError.NOT_PENDING.getMessage());
	}

	@Test
	@DisplayName("리뷰 작성 완료 시 리뷰 상태가 COMPLETED로 변경된다")
	void completeReview_success() {
		// given
		Rental rental = RentalBuilder.create();
		rental.active();
		rental.inReturn();
		rental.complete();

		// when
		rental.completeReview();

		// then
		assertThat(rental.getRentalStatus()).isEqualTo(RentalStatus.COMPLETED);
		assertThat(rental.getReviewStatus()).isEqualTo(ReviewStatus.COMPLETED);
	}

	@Test
	@DisplayName("리뷰 상태가 COMPLETE가 아니면 예외가 발생한다")
	void completeReview_notCompleted() {
		// given
		Rental rental = RentalBuilder.create();

		// when & then
		assertThatThrownBy(() -> rental.completeReview())
			.isInstanceOf(DomainException.class)
			.hasMessage(RentalError.NOT_COMPLETED.getMessage());
	}
}