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
import ok.cherry.payment.domain.Payment;
import ok.cherry.payment.domain.type.PaymentMethod;
import ok.cherry.payment.infrastructure.PaymentRepository;
import ok.cherry.product.ProductBuilder;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.type.Color;
import ok.cherry.product.infrastructure.ProductRepository;
import ok.cherry.rental.application.request.AddressRequest;
import ok.cherry.rental.application.request.PlaceRentalOrderRequest;
import ok.cherry.rental.application.request.ShippingInfoRequest;
import ok.cherry.rental.application.response.PlaceRentalOrderResponse;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.exception.RentalError;
import ok.cherry.rental.infrastructure.RentalRepository;
import ok.cherry.shipping.domain.Shipping;
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