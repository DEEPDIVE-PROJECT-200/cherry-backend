package ok.cherry.payment.application;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.member.infrastructure.MemberRepository;
import ok.cherry.payment.PaymentBuilder;
import ok.cherry.payment.application.dto.response.PaymentItemResponse;
import ok.cherry.payment.application.dto.response.PaymentResponse;
import ok.cherry.payment.domain.Payment;
import ok.cherry.payment.domain.PaymentItem;
import ok.cherry.payment.domain.status.PaymentStatus;
import ok.cherry.payment.domain.type.PaymentMethod;
import ok.cherry.payment.exception.PaymentError;
import ok.cherry.payment.infrastructure.PaymentRepository;
import ok.cherry.product.ProductBuilder;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.type.Brand;
import ok.cherry.product.domain.type.Color;
import ok.cherry.product.infrastructure.ProductRepository;
import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.RentalItemBuilder;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.RentalItem;
import ok.cherry.rental.infrastructure.RentalRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PaymentServiceTest {

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RentalRepository rentalRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	EntityManager entityManager;

	@Test
	@DisplayName("존재하는 결제 ID와 올바른 providerId로 결제 조회에 성공한다")
	void getPaymentWithValidIdAndProviderIdSuccessfully() {
		// given
		String providerId = "kakao_12345";
		Member savedMember = memberRepository.save(MemberBuilder.builder().withProviderId(providerId).build());
		Product savedProduct = productRepository.save(ProductBuilder.create());

		RentalItem rentalItem = RentalItemBuilder.builder()
			.withProduct(savedProduct)
			.build();

		Rental savedRental = rentalRepository.save(RentalBuilder.builder()
			.withMember(savedMember)
			.withRentalItems(List.of(rentalItem))
			.build());

		Payment savedPayment = paymentRepository.save(PaymentBuilder.builder()
			.withMember(savedMember)
			.withRental(savedRental)
			.build());

		// when
		PaymentResponse response = paymentService.getPayment(savedPayment.getId(), providerId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.paymentId()).isEqualTo(savedPayment.getId());
		assertThat(response.memberId()).isEqualTo(savedMember.getId());
		assertThat(response.rentalId()).isEqualTo(savedRental.getId());
		assertThat(response.totalAmount()).isEqualTo(savedPayment.getPaymentAmount().getTotalAmount());
		assertThat(response.paymentMethod()).isEqualTo(savedPayment.getPaymentInfo().getPaymentMethod());
		assertThat(response.paymentStatus()).isEqualTo(savedPayment.getPaymentInfo().getStatus());
		assertThat(response.items()).hasSize(savedPayment.getPaymentItems().size());
		assertThat(response.additionalFee()).isNotNull();
	}

	@Test
	@DisplayName("존재하지 않는 결제 ID로 조회 시 예외가 발생한다")
	void throwExceptionWhenPaymentNotFound() {
		// given
		Long nonExistentPaymentId = 999L;
		String providerId = "kakao_12345";

		// when & then
		assertThatThrownBy(() -> paymentService.getPayment(nonExistentPaymentId, providerId))
			.isInstanceOf(BusinessException.class)
			.hasMessage(PaymentError.PAYMENT_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("다른 사용자의 결제 정보 조회 시 접근 거부 예외가 발생한다")
	void throwExceptionWhenAccessingOtherUserPayment() {
		// given
		String paymentOwnerProviderId = "kakao_67890";
		String requestProviderId = "kakao_12345";

		Member paymentOwner = memberRepository.save(
			MemberBuilder.builder().withProviderId(paymentOwnerProviderId).build());
		Product savedProduct = productRepository.save(ProductBuilder.create());

		RentalItem rentalItem = RentalItemBuilder.builder()
			.withProduct(savedProduct)
			.build();

		Rental savedRental = rentalRepository.save(RentalBuilder.builder()
			.withMember(paymentOwner)
			.withRentalItems(List.of(rentalItem))
			.build());

		Payment savedPayment = paymentRepository.save(PaymentBuilder.builder()
			.withMember(paymentOwner)
			.withRental(savedRental)
			.build());

		// when & then
		assertThatThrownBy(() -> paymentService.getPayment(savedPayment.getId(), requestProviderId))
			.isInstanceOf(BusinessException.class)
			.hasMessage(PaymentError.PAYMENT_ACCESS_DENIED.getMessage());
	}

	@Test
	@DisplayName("결제 생성에 성공한다")
	void createPaymentSuccessfully() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());

		RentalItem rentalItem = RentalItemBuilder.builder()
			.withProduct(savedProduct)
			.build();

		Rental savedRental = rentalRepository.save(RentalBuilder.builder()
			.withMember(savedMember)
			.withRentalItems(List.of(rentalItem))
			.build());

		PaymentMethod paymentMethod = PaymentMethod.KAKAO_PAY;
		BigDecimal shippingFee = BigDecimal.valueOf(3000);
		BigDecimal cleaningFee = BigDecimal.valueOf(2000);

		// when
		Payment result = paymentService.createPayment(
			savedMember, savedRental, paymentMethod, shippingFee, cleaningFee);
		flushAndClear();

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isNotNull();
		assertThat(result.getMember().getId()).isEqualTo(savedMember.getId());
		assertThat(result.getRental().getId()).isEqualTo(savedRental.getId());
		assertThat(result.getPaymentInfo().getPaymentMethod()).isEqualTo(paymentMethod);
		assertThat(result.getPaymentAmount().getAdditionalFee().getShippingFee()).isEqualTo(shippingFee);
		assertThat(result.getPaymentAmount().getAdditionalFee().getCleaningFee()).isEqualTo(cleaningFee);
		assertThat(result.getPaymentInfo().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
		assertThat(result.isCompleted()).isTrue();

		Payment savedPayment = paymentRepository.findById(result.getId()).orElseThrow();
		assertThat(savedPayment.getPaymentInfo().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
	}

	@Test
	@DisplayName("단일 및 복수 결제 아이템들의 응답이 올바르게 생성된다")
	void generatePaymentItemsResponseCorrectly() {
		// given
		String providerId = "kakao_12345";
		Member savedMember = memberRepository.save(
			MemberBuilder.builder().withProviderId(providerId).build());

		// 여러 개의 상품 생성
		Product product1 = productRepository.save(ProductBuilder.builder()
			.withName("SONY WH-1000XM5")
			.withBrand(Brand.SONY)
			.build());
		Product product2 = productRepository.save(ProductBuilder.builder()
			.withName("Apple AirPods Pro")
			.withBrand(Brand.APPLE)
			.build());
		Product product3 = productRepository.save(ProductBuilder.builder()
			.withName("Bose QuietComfort 45")
			.withBrand(Brand.BOSE)
			.build());

		// 여러 개의 대여 아이템 생성 (다른 색상, 가격)
		RentalItem rentalItem1 = RentalItemBuilder.builder().withProduct(product1)
			.withColor(Color.BLACK)
			.withPrice(BigDecimal.valueOf(15000))
			.build();

		RentalItem rentalItem2 = RentalItemBuilder.builder()
			.withProduct(product2)
			.withColor(Color.WHITE)
			.withPrice(BigDecimal.valueOf(12000))
			.build();

		RentalItem rentalItem3 = RentalItemBuilder.builder()
			.withProduct(product3)
			.withColor(Color.MIDNIGHT_BLUE)
			.withPrice(BigDecimal.valueOf(18000))
			.build();

		Rental savedRental = rentalRepository.save(RentalBuilder.builder()
			.withMember(savedMember)
			.withRentalItems(List.of(rentalItem1, rentalItem2, rentalItem3))
			.build());

		Payment savedPayment = paymentRepository.save(PaymentBuilder.builder()
			.withMember(savedMember)
			.withRental(savedRental)
			.build());

		// when
		PaymentResponse response = paymentService.getPayment(savedPayment.getId(), providerId);

		// then
		assertThat(response.items()).hasSize(3);

		// 모든 아이템들이 올바르게 매핑되었는지 검증
		List<PaymentItemResponse> responseItems = response.items();
		List<PaymentItem> paymentItems = savedPayment.getPaymentItems();

		for (int i = 0; i < responseItems.size(); i++) {
			PaymentItemResponse responseItem = responseItems.get(i);
			PaymentItem paymentItem = paymentItems.get(i);

			assertThat(responseItem.productName()).isEqualTo(paymentItem.getProductName());
			assertThat(responseItem.brand()).isEqualTo(paymentItem.getBrand());
			assertThat(responseItem.color()).isEqualTo(paymentItem.getColor());
			assertThat(responseItem.quantity()).isEqualTo(paymentItem.getQuantity());
			assertThat(responseItem.price()).isEqualTo(paymentItem.getTotalPrice());
		}

		// 특정 상품들이 포함되었는지 검증
		assertThat(responseItems)
			.extracting(PaymentItemResponse::productName)
			.containsExactly("SONY WH-1000XM5", "Apple AirPods Pro", "Bose QuietComfort 45");

		assertThat(responseItems)
			.extracting(PaymentItemResponse::brand)
			.containsExactly(Brand.SONY, Brand.APPLE, Brand.BOSE);

		assertThat(responseItems)
			.extracting(PaymentItemResponse::color)
			.containsExactly(Color.BLACK, Color.WHITE, Color.MIDNIGHT_BLUE);

		assertThat(responseItems)
			.extracting(PaymentItemResponse::price)
			.containsExactly(
				BigDecimal.valueOf(15000),
				BigDecimal.valueOf(12000),
				BigDecimal.valueOf(18000)
			);
	}

	@Test
	@DisplayName("추가 요금 응답이 올바르게 생성된다")
	void generateAdditionalFeeResponseCorrectly() {
		// given
		String providerId = "kakao_12345";
		BigDecimal shippingFee = BigDecimal.valueOf(5000);
		BigDecimal cleaningFee = BigDecimal.valueOf(3000);

		Member savedMember = memberRepository.save(
			MemberBuilder.builder().withProviderId(providerId).build());
		Product savedProduct = productRepository.save(ProductBuilder.create());

		RentalItem rentalItem = RentalItemBuilder.builder()
			.withProduct(savedProduct)
			.build();

		Rental savedRental = rentalRepository.save(RentalBuilder.builder()
			.withMember(savedMember)
			.withRentalItems(List.of(rentalItem))
			.build());

		Payment savedPayment = paymentRepository.save(PaymentBuilder.builder()
			.withMember(savedMember)
			.withRental(savedRental)
			.withAdditionalFee(shippingFee, cleaningFee)
			.build());

		// when
		PaymentResponse response = paymentService.getPayment(savedPayment.getId(), providerId);

		// then
		assertThat(response.additionalFee()).isNotNull();
		assertThat(response.additionalFee().shippingFee()).isEqualTo(shippingFee);
		assertThat(response.additionalFee().cleaningFee()).isEqualTo(cleaningFee);
	}

	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}