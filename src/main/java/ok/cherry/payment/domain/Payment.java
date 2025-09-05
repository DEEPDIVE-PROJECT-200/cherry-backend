package ok.cherry.payment.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.persistence.OrderColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ok.cherry.member.domain.Member;
import ok.cherry.payment.domain.status.PaymentStatus;
import ok.cherry.payment.domain.type.PaymentMethod;
import ok.cherry.rental.domain.Rental;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rental_id", nullable = false)
	private Rental rental;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentMethod paymentMethod;

	@Column(nullable = false)
	private BigDecimal rentalAmount;

	@Column(nullable = false)
	private BigDecimal totalAmount;

	@Column(nullable = false)
	private LocalDateTime rentalStartAt;

	@Column(nullable = false)
	private LocalDateTime rentalEndAt;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(
		name = "payment_items",
		joinColumns = @JoinColumn(name = "payment_id", nullable = false)
	)
	@OrderColumn(name = "payment_item_idx")
	private List<PaymentItem> paymentItems = new ArrayList<>();

	@Embedded
	private AdditionalFee additionalFee;

	@Embedded
	private PaymentDetail detail;

	@SuppressWarnings("checkstyle:RegexpSingleline")
	public static Payment create(
		Member member,
		Rental rental,
		PaymentMethod paymentMethod,
		BigDecimal shippingFee,
		BigDecimal cleaningFee
	) {
		Payment payment = new Payment();
		payment.member = member;
		payment.rental = rental;
		payment.paymentMethod = paymentMethod;
		payment.status = PaymentStatus.PENDING;
		payment.rentalStartAt = rental.getDetail().getStartAt();
		payment.rentalEndAt = rental.getDetail().getEndAt();

		payment.rentalAmount = rental.getTotalPrice();
		payment.additionalFee = AdditionalFee.create(shippingFee, cleaningFee);
		payment.totalAmount = payment.rentalAmount.add(payment.additionalFee.getTotal());
		payment.createPaymentItemsFromRental(rental);

		payment.detail = PaymentDetail.create();
		return payment;
	}

	private void createPaymentItemsFromRental(Rental rental) {
		List<PaymentItem> paymentItems = rental.getRentalItems().stream()
			.map(rentalItem -> PaymentItem.create(
				rentalItem.getProduct().getName(),
				rentalItem.getProduct().getBrand(),
				rentalItem.getColor(),
				1,
				rentalItem.getPrice()
			))
			.toList();

		this.paymentItems.addAll(paymentItems);
	}

	public void complete() {
		this.status = PaymentStatus.COMPLETED;
	}
}
