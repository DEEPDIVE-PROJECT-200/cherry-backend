package ok.cherry.rental.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

	@Embedded
	private RentalDetail detail;

	public static Rental create(
		Member member,
		List<RentalItem> items,
		String rentalNumber,
		LocalDateTime startAt,
		LocalDateTime endAt
	) {
		validateRentalNumber(rentalNumber);

		Rental rental = new Rental();
		rental.member = member;
		rental.totalPrice = calculateTotalPrice(items);
		rental.detail = RentalDetail.create(startAt, endAt);
		rental.rentalNumber = rentalNumber;
		rental.rentalStatus = RentalStatus.PENDING;

		items.forEach(item -> {
			item.setRental(rental);
			rental.rentalItems.add(item);
		});

		return rental;
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
}