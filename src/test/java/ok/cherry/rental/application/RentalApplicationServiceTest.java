package ok.cherry.rental.application;

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
import ok.cherry.cart.CartBuilder;
import ok.cherry.cart.domain.Cart;
import ok.cherry.cart.infrastructure.CartRepository;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.member.infrastructure.MemberRepository;
import ok.cherry.payment.application.dto.response.PaymentResponse;
import ok.cherry.payment.domain.Payment;
import ok.cherry.payment.domain.type.PaymentMethod;
import ok.cherry.payment.infrastructure.PaymentRepository;
import ok.cherry.product.ProductBuilder;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.type.Color;
import ok.cherry.product.infrastructure.ProductRepository;
import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.RentalItemBuilder;
import ok.cherry.rental.application.request.AddressRequest;
import ok.cherry.rental.application.request.PlaceRentalOrderRequest;
import ok.cherry.rental.application.request.ShippingInfoRequest;
import ok.cherry.rental.application.response.PlaceRentalOrderResponse;
import ok.cherry.rental.application.response.RentalCompleteResponse;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.RentalItem;
import ok.cherry.rental.domain.status.RentalStatus;
import ok.cherry.rental.domain.status.ReviewStatus;
import ok.cherry.rental.exception.RentalError;
import ok.cherry.rental.infrastructure.RentalRepository;
import ok.cherry.shipping.ShippingBuilder;
import ok.cherry.shipping.domain.Shipping;
import ok.cherry.shipping.domain.type.Direction;
import ok.cherry.shipping.exception.ShippingError;
import ok.cherry.shipping.infrastructure.ShippingRepository;

@SpringBootTest
@Transactional
class RentalApplicationServiceTest {

	@Autowired
	private RentalApplicationService rentalApplicationService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private RentalRepository rentalRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private ShippingRepository shippingRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	@DisplayName("직접 대여 주문 생성(단건 바로 결제)에 성공한다")
	void placeRentalOrder_directRental_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());

		LocalDate startAt = LocalDate.now();
		LocalDate endAt = startAt.plusDays(7);

		PlaceRentalOrderRequest request = createDirectRentalRequest(
			savedProduct.getId(), startAt, endAt, Color.BLACK, PaymentMethod.KAKAO_PAY
		);
		flushAndClear();

		// when
		PlaceRentalOrderResponse response = rentalApplicationService.placeRentalOrder(
			savedMember.getProviderId(), request
		);

		// then
		assertThat(response.rentalId()).isNotNull();
		assertThat(response.rentalNumber()).isNotNull();
		assertThat(response.paymentId()).isNotNull();
		assertThat(response.shippingId()).isNotNull();
		assertThat(response.totalAmount()).isNotNull();

		// 대여 검증
		Rental savedRental = rentalRepository.findById(response.rentalId()).orElseThrow();
		assertThat(savedRental.getMember().getId()).isEqualTo(savedMember.getId());
		assertThat(savedRental.getRentalItems()).hasSize(1);
		assertThat(savedRental.getRentalItems().getFirst().getProduct().getId()).isEqualTo(savedProduct.getId());
		assertThat(savedRental.getRentalItems().getFirst().getColor()).isEqualTo(Color.BLACK);

		// 결제 검증
		Payment savedPayment = paymentRepository.findById(response.paymentId()).orElseThrow();
		assertThat(savedPayment.getMember().getId()).isEqualTo(savedMember.getId());
		assertThat(savedPayment.getRental().getId()).isEqualTo(savedRental.getId());

		// 배송 검증
		Shipping savedShipping = shippingRepository.findById(response.shippingId()).orElseThrow();
		assertThat(savedShipping.getMember().getId()).isEqualTo(savedMember.getId());
		assertThat(savedShipping.getRental().getId()).isEqualTo(savedRental.getId());
	}

	@Test
	@DisplayName("장바구니 대여 주문 생성에 성공한다")
	void placeRentalOrder_cartRental_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product product1 = productRepository.save(ProductBuilder.builder()
			.withDailyRentalPrice(BigDecimal.valueOf(10000))
			.build());
		Product product2 = productRepository.save(ProductBuilder.builder()
			.withName("Product2")
			.withDailyRentalPrice(BigDecimal.valueOf(15000))
			.build());

		Cart cart1 = cartRepository.save(CartBuilder.builder()
			.withMember(savedMember)
			.withProduct(product1)
			.withPrice(product1.getDailyRentalPrice())
			.withColor(Color.BLACK)
			.build());

		Cart cart2 = cartRepository.save(CartBuilder.builder()
			.withMember(savedMember)
			.withProduct(product2)
			.withPrice(product2.getDailyRentalPrice())
			.withColor(Color.WHITE)
			.build());

		LocalDate startAt = LocalDate.now();
		LocalDate endAt = startAt.plusDays(6); // 7일간 대여

		PlaceRentalOrderRequest request = createCartRentalRequest(
			List.of(cart1.getId(), cart2.getId()), startAt, endAt, PaymentMethod.KAKAO_PAY
		);

		// when
		PlaceRentalOrderResponse response = rentalApplicationService.placeRentalOrder(
			savedMember.getProviderId(), request
		);
		flushAndClear();

		// then
		assertThat(response.rentalId()).isNotNull();
		assertThat(response.paymentId()).isNotNull();
		assertThat(response.shippingId()).isNotNull();

		// 대여 검증
		Rental savedRental = rentalRepository.findById(response.rentalId()).orElseThrow();
		assertThat(savedRental.getRentalItems()).hasSize(2);

		// 각 대여 아이템 금액 검증
		BigDecimal expectedProduct1Amount = BigDecimal.valueOf(10000).multiply(BigDecimal.valueOf(7)); // 70,000원
		BigDecimal expectedProduct2Amount = BigDecimal.valueOf(15000).multiply(BigDecimal.valueOf(7)); // 105,000원
		BigDecimal expectedTotalAmount = expectedProduct1Amount.add(expectedProduct2Amount); // 175,000원

		assertThat(savedRental.getRentalItems())
			.extracting("price")
			.usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
			.containsExactlyInAnyOrder(expectedProduct1Amount, expectedProduct2Amount);

		assertThat(savedRental.getTotalPrice()).isEqualByComparingTo(expectedTotalAmount);

		// 결제 금액 검증
		Payment savedPayment = paymentRepository.findById(response.paymentId()).orElseThrow();
		assertThat(savedPayment.getMember().getId()).isEqualTo(savedMember.getId());
		assertThat(savedPayment.getRental().getId()).isEqualTo(savedRental.getId());
		assertThat(savedPayment.getPaymentAmount().getTotalAmount()).isEqualByComparingTo(expectedTotalAmount);

		// 장바구니 삭제 검증
		assertThat(cartRepository.findById(cart1.getId())).isEmpty();
		assertThat(cartRepository.findById(cart2.getId())).isEmpty();
	}

	@Test
	@DisplayName("직접 대여와 장바구니 대여가 모두 설정된 경우 예외가 발생한다")
	void placeRentalOrder_ambiguousRequest_throwsException() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		Cart savedCart = cartRepository.save(CartBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct)
			.build());

		PlaceRentalOrderRequest request = new PlaceRentalOrderRequest(
			savedProduct.getId(),  // productId
			Color.BLACK,  // color
			List.of(savedCart.getId()),  // cartIds
			LocalDate.now(),  // rentStartAt
			LocalDate.now().plusDays(7),  // rentEndAt
			createShippingInfo(),  // shippingInfo
			PaymentMethod.KAKAO_PAY   // paymentMethod
		);

		// when & then
		assertThatThrownBy(() -> rentalApplicationService.placeRentalOrder(savedMember.getProviderId(), request))
			.isInstanceOf(BusinessException.class)
			.hasMessage(RentalError.AMBIGUOUS_RENTAL_REQUEST.getMessage());
	}

	@Test
	@DisplayName("직접 대여와 장바구니 대여가 모두 설정되지 않은 경우 예외가 발생한다")
	void placeRentalOrder_invalidRequest_throwsException() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());

		PlaceRentalOrderRequest request = new PlaceRentalOrderRequest(
			null,  // productId
			null,  // color
			null,  // cartIds
			LocalDate.now(),  // rentStartAt
			LocalDate.now().plusDays(7),  // rentEndAt
			createShippingInfo(),  // shippingInfo
			PaymentMethod.KAKAO_PAY  // paymentMethod
		);

		// when & then
		assertThatThrownBy(() -> rentalApplicationService.placeRentalOrder(savedMember.getProviderId(), request))
			.isInstanceOf(BusinessException.class)
			.hasMessage(RentalError.INVALID_RENTAL_REQUEST.getMessage());
	}

	@Test
	@DisplayName("대여 기간이 7일인 경우 올바른 총 금액이 계산된다")
	void placeRentalOrder_calculateCorrectTotalAmount() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.builder()
			.withDailyRentalPrice(BigDecimal.valueOf(10000))
			.build());

		LocalDate startAt = LocalDate.now();
		LocalDate endAt = startAt.plusDays(6); // 7일간 대여

		PlaceRentalOrderRequest request = createDirectRentalRequest(
			savedProduct.getId(), startAt, endAt, Color.BLACK, PaymentMethod.KAKAO_PAY
		);

		// when
		PlaceRentalOrderResponse response = rentalApplicationService.placeRentalOrder(
			savedMember.getProviderId(), request
		);
		flushAndClear();

		// then
		Rental savedRental = rentalRepository.findById(response.rentalId()).orElseThrow();
		BigDecimal expectedAmount = BigDecimal.valueOf(10000).multiply(BigDecimal.valueOf(7)); // 7일 * 10000원
		assertThat(savedRental.getRentalItems().getFirst().getPrice()).isEqualByComparingTo(expectedAmount);
	}

	@Test
	@DisplayName("반납 배송이 완료되면 대여상태와 리뷰상태가 각각 COMPLETED, AVAILABLE 로 변경된다")
	void completeRental_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		RentalItem rentalItem = RentalItemBuilder.builder().withProduct(savedProduct).build();
		Rental rental = RentalBuilder.builder()
			.withMember(savedMember)
			.withRentalItems(List.of(rentalItem))
			.build();
		rental.active();
		rental.inReturn();
		rentalRepository.save(rental);

		Shipping shipping = ShippingBuilder.builder()
			.withRental(rental)
			.withDirection(Direction.INBOUND)
			.build();
		shipping.startShipping();
		shipping.completeShipping();
		shippingRepository.save(shipping);
		flushAndClear();

		// when
		RentalCompleteResponse response = rentalApplicationService.completeRental(rental.getId());

		// then
		Rental updatedRental = rentalRepository.findById(rental.getId()).orElseThrow();
		assertThat(updatedRental.getRentalStatus()).isEqualTo(RentalStatus.COMPLETED);
		assertThat(updatedRental.getReviewStatus()).isEqualTo(ReviewStatus.AVAILABLE);

		assertThat(response.rentalId()).isEqualTo(updatedRental.getId());
		assertThat(response.status()).isEqualTo(RentalStatus.COMPLETED);
	}

	@Test
	@DisplayName("상태를 변경하려는 대여를 찾을 수 없어 예외가 발생한다")
	void completeRental_fail_rental_not_found() {
		// given
		Long notExistRentalId = 999L;

		// when & then
		assertThatThrownBy(() -> rentalApplicationService.completeRental(notExistRentalId))
			.isInstanceOf(BusinessException.class)
			.hasMessage(RentalError.RENTAL_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("반납 배송 정보를 찾을 수 없어 예외가 발생한다")
	void completeRental_fail_return_shipping_not_found() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		RentalItem rentalItem = RentalItemBuilder.builder().withProduct(savedProduct).build();
		Rental savedRental = rentalRepository.save(
			RentalBuilder.builder()
				.withMember(savedMember)
				.withRentalItems(List.of(rentalItem))
				.build()
		);

		// when & then
		assertThatThrownBy(() -> rentalApplicationService.completeRental(savedRental.getId()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ShippingError.RETURN_SHIPPING_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("반납 배송이 아직 완료되지 않은 상태면 예외가 발생한다")
	void completeRental_fail_return_shipping_not_delivered() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		RentalItem rentalItem = RentalItemBuilder.builder().withProduct(savedProduct).build();
		Rental rental = RentalBuilder.builder()
			.withMember(savedMember)
			.withRentalItems(List.of(rentalItem))
			.build();
		rental.active();
		rental.inReturn();
		rentalRepository.save(rental);

		Shipping shipping = ShippingBuilder.builder()
			.withRental(rental)
			.withDirection(Direction.INBOUND)
			.build();
		shipping.startShipping();
		shippingRepository.save(shipping);

		// when & then
		assertThatThrownBy(() -> rentalApplicationService.completeRental(rental.getId()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ShippingError.RETURN_SHIPPING_NOT_COMPLETED.getMessage());
	}

	@Test
	@DisplayName("대여 Id로 관련된 결제 정보를 가져온다")
	void getPaymentByRentalId_success() {
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());

		LocalDate startAt = LocalDate.now();
		LocalDate endAt = startAt.plusDays(6);

		PlaceRentalOrderRequest request = createDirectRentalRequest(
			savedProduct.getId(), startAt, endAt, Color.BLACK, PaymentMethod.KAKAO_PAY
		);

		PlaceRentalOrderResponse orderResponse = rentalApplicationService.placeRentalOrder(
			savedMember.getProviderId(), request
		);
		flushAndClear();

		// when
		PaymentResponse response = rentalApplicationService.getPaymentByRentalId(
			orderResponse.rentalId(),
			savedMember.getProviderId()
		);

		// then
		assertThat(response.paymentId()).isNotNull();
		assertThat(response.memberId()).isEqualTo(savedMember.getId());
		assertThat(response.rentalId()).isEqualTo(orderResponse.rentalId());
		assertThat(response.paymentMethod()).isEqualTo(PaymentMethod.KAKAO_PAY);
		assertThat(response.items()).hasSize(1);
	}

	@Test
	@DisplayName("대여 Id로 결제 조회 시 대상 대여가 존재하지 않으면 예외가 발생한다")
	void getPaymentByRentalId_fail_rental_not_found() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Long notExistRentalId = 999L;

		// when & then
		assertThatThrownBy(() -> rentalApplicationService.getPaymentByRentalId(
			notExistRentalId,
			savedMember.getProviderId()
		))
			.isInstanceOf(BusinessException.class)
			.hasMessage(RentalError.RENTAL_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("대여 Id로 결제 조회 시 소유자가 아니면 접근이 거부 된다")
	void getPaymentByRentalId_fail_forbidden_access() {
		// given
		Member memberA = memberRepository.save(MemberBuilder.create());
		Member memberB = memberRepository.save(MemberBuilder.builder()
			.withEmail("other@other.com")
			.withNickname("other")
			.withProviderId("other")
			.build()
		);
		Product savedProduct = productRepository.save(ProductBuilder.create());

		PlaceRentalOrderRequest request = createDirectRentalRequest(
			savedProduct.getId(), LocalDate.now(), LocalDate.now().plusDays(6), Color.BLACK, PaymentMethod.KAKAO_PAY
		);
		PlaceRentalOrderResponse orderResponse = rentalApplicationService.placeRentalOrder(
			memberA.getProviderId(), request
		);

		// when & then
		assertThatThrownBy(() -> rentalApplicationService.getPaymentByRentalId(
			orderResponse.rentalId(),
			memberB.getProviderId()
		))
			.isInstanceOf(BusinessException.class)
			.hasMessage(RentalError.FORBIDDEN_ACCESS.getMessage());
	}

	@Test
	@DisplayName("대여 Id로 결제 조회 시 결제가 존재하지 않으면 예외가 발생한다")
	void getPaymentByRentalId_fail_payment_not_found() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		RentalItem rentalItem = RentalItemBuilder.builder().withProduct(savedProduct).build();
		Rental savedRental = rentalRepository.save(
			RentalBuilder.builder()
				.withMember(savedMember)
				.withRentalItems(List.of(rentalItem))
				.build()
		);

		// when & then
		assertThatThrownBy(() -> rentalApplicationService.getPaymentByRentalId(
			savedRental.getId(),
			savedMember.getProviderId()
		))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ok.cherry.payment.exception.PaymentError.PAYMENT_NOT_FOUND.getMessage());
	}

	private PlaceRentalOrderRequest createDirectRentalRequest(
		Long productId, LocalDate startAt, LocalDate endAt, Color color, PaymentMethod paymentMethod
	) {
		return new PlaceRentalOrderRequest(
			productId,
			color,
			null,  // cartIds
			startAt,
			endAt,
			createShippingInfo(),
			paymentMethod
		);
	}

	private PlaceRentalOrderRequest createCartRentalRequest(
		List<Long> cartIds, LocalDate startAt, LocalDate endAt, PaymentMethod paymentMethod
	) {
		return new PlaceRentalOrderRequest(
			null,  // productId
			null,  // color
			cartIds,
			startAt,
			endAt,
			createShippingInfo(),
			paymentMethod
		);
	}

	private ShippingInfoRequest createShippingInfo() {
		return new ShippingInfoRequest(
			"홍길동",
			"010-1234-5678",
			new AddressRequest("12345", "서울시 강남구", "상세주소")
		);
	}

	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}