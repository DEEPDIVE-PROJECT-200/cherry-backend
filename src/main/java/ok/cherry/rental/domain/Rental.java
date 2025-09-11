package ok.cherry.rental.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ok.cherry.global.exception.error.DomainException;
import ok.cherry.member.domain.Member;
import ok.cherry.rental.domain.status.RentalStatus;
import ok.cherry.rental.domain.status.ReviewStatus;
import ok.cherry.rental.exception.RentalError;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rental {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(nullable = false)
	private BigDecimal totalPrice;

	@Column(nullable = false, unique = true)
	private String rentalNumber;

	@OneToMany(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RentalItem> rentalItems = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RentalStatus rentalStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReviewStatus reviewStatus;

	@Embedded
	private RentalDetail detail;

	public static Rental create(
		Member member,
		List<RentalItem> items,
		String rentalNumber,
		LocalDate startAt,
		LocalDate endAt
	) {
		validateRentalNumber(rentalNumber);

		Rental rental = new Rental();
		rental.member = member;
		rental.totalPrice = calculateTotalPrice(items);
		rental.detail = RentalDetail.create(startAt, endAt);
		rental.rentalNumber = rentalNumber;
		rental.rentalStatus = RentalStatus.PENDING;
		rental.reviewStatus = ReviewStatus.PENDING;

		items.forEach(item -> {
			item.setRental(rental);
			rental.rentalItems.add(item);
		});

		return rental;
	}

	/**
	 * 생성: rentalStatus:PENDING, reviewStatus:PENDING
	 * 배송 완료: rentalStatus:ACTIVE
	 * 체험 종료(반납 신청): rentalStatus:IN_RETURN
	 * 반납 완료: rentalStatus:COMPLETED, reviewStatus:AVAILABLE
	 * 리뷰 작성: reviewStatus:COMPLETED
	 */
	public void active() {
		validateIsPending();
		this.rentalStatus = RentalStatus.ACTIVE;
	}

	public void inReturn() {
		validateIsActive();
		this.rentalStatus = RentalStatus.IN_RETURN;
	}

	public void complete() {
		validateIsInReturn();
		this.reviewStatus = ReviewStatus.AVAILABLE;
		this.rentalStatus = RentalStatus.COMPLETED;
	}

	public void completeReview() {
		validateIsCompleted();
		validateReviewStatusIsAvailable();
		this.reviewStatus = ReviewStatus.COMPLETED;
	}

	private static BigDecimal calculateTotalPrice(List<RentalItem> items) {
		return items.stream()
			.map(RentalItem::getPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private static void validateRentalNumber(String rentalNumber) {
		if (rentalNumber == null || !rentalNumber.matches("^CH-\\d{20}$")) {
			throw new DomainException(RentalError.INVALID_RENTAL_NUMBER);
		}
	}

	private void validateIsPending() {
		if (this.rentalStatus != RentalStatus.PENDING) {
			throw new DomainException(RentalError.NOT_PENDING);
		}
	}

	private void validateIsActive() {
		if (this.rentalStatus != RentalStatus.ACTIVE) {
			throw new DomainException(RentalError.NOT_ACTIVE);
		}
	}

	private void validateIsInReturn() {
		if (this.rentalStatus != RentalStatus.IN_RETURN) {
			throw new DomainException(RentalError.NOT_IN_RETURN);
		}
	}

	private void validateIsCompleted() {
		if (this.rentalStatus != RentalStatus.COMPLETED) {
			throw new DomainException(RentalError.NOT_COMPLETED);
		}
	}

	private void validateReviewStatusIsAvailable() {
		if (this.reviewStatus != ReviewStatus.AVAILABLE) {
			throw new DomainException(RentalError.NOT_REVIEW_STATUS_AVAILABLE);
		}
	}
}