package ok.cherry.shipping.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import ok.cherry.global.exception.error.DomainException;
import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.member.infrastructure.MemberRepository;
import ok.cherry.product.ProductBuilder;
import ok.cherry.product.domain.Product;
import ok.cherry.product.infrastructure.ProductRepository;
import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.status.RentalStatus;
import ok.cherry.rental.infrastructure.RentalRepository;
import ok.cherry.shipping.ShippingBuilder;
import ok.cherry.shipping.application.command.CreateShippingCommand;
import ok.cherry.shipping.domain.Address;
import ok.cherry.shipping.domain.Shipping;
import ok.cherry.shipping.domain.status.ShippingStatus;
import ok.cherry.shipping.domain.type.Direction;
import ok.cherry.shipping.exception.ShippingError;
import ok.cherry.shipping.infrastructure.ShippingRepository;

@SpringBootTest
@Transactional
class ShippingServiceTest {

	@Autowired
	private ShippingService shippingService;

	@Autowired
	private ShippingRepository shippingRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RentalRepository rentalRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	@DisplayName("배송 생성에 성공한다")
	void createShipping_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		Rental savedRental = rentalRepository.save(RentalBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct)
			.build());

		CreateShippingCommand command = CreateShippingCommand.of(
			Direction.OUTBOUND,
			"홍길동",
			"010-1234-5678",
			new Address("12345", "서울시 강남구 테헤란로 123", "456호")
		);

		// when
		Shipping shipping = shippingService.createShipping(savedMember, savedRental, command);
		flushAndClear();

		// then
		Shipping savedShipping = shippingRepository.findById(shipping.getId()).orElseThrow();
		assertThat(savedShipping.getMember().getId()).isEqualTo(savedMember.getId());
		assertThat(savedShipping.getRental().getId()).isEqualTo(savedRental.getId());
		assertThat(savedShipping.getDirection()).isEqualTo(Direction.OUTBOUND);
		assertThat(savedShipping.getStatus()).isEqualTo(ShippingStatus.PENDING);
		assertThat(savedShipping.getShippingInfo().getReceiver()).isEqualTo("홍길동");
		assertThat(savedShipping.getShippingInfo().getPhoneNumber()).isEqualTo("010-1234-5678");
		assertThat(savedShipping.getShippingInfo().getAddress()).isNotNull();
		assertThat(savedShipping.getTrackingNumber()).matches("^\\d{20}$");
		assertThat(savedShipping.getDetail().getCreatedAt()).isNotNull();
	}

	@Test
	@DisplayName("배송 생성 시 운송장 번호가 생성된다")
	void createShipping_generateTrackingNumber() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		Rental savedRental = rentalRepository.save(RentalBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct)
			.build());

		CreateShippingCommand command = CreateShippingCommand.of(
			Direction.OUTBOUND,
			"김철수",
			"010-9876-5432",
			new Address("54321", "부산시 해운대구 해운대로 456", "789호")
		);

		// when
		Shipping shipping = shippingService.createShipping(savedMember, savedRental, command);

		// then
		assertThat(shipping.getTrackingNumber()).isNotNull();
		assertThat(shipping.getTrackingNumber()).matches("^\\d{20}$");
	}

	@Test
	@DisplayName("반입 배송 생성에 성공한다")
	void createInboundShipping_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		Rental savedRental = rentalRepository.save(RentalBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct)
			.build());

		CreateShippingCommand command = CreateShippingCommand.of(
			Direction.INBOUND,
			"이영희",
			"010-5555-6666",
			new Address("67890", "대구시 중구 동성로 789", "101동 202호")
		);

		// when
		Shipping shipping = shippingService.createShipping(savedMember, savedRental, command);
		flushAndClear();

		// then
		Shipping savedShipping = shippingRepository.findById(shipping.getId()).orElseThrow();
		assertThat(savedShipping.getDirection()).isEqualTo(Direction.INBOUND);
		assertThat(savedShipping.getShippingInfo().getReceiver()).isEqualTo("이영희");
		assertThat(savedShipping.getShippingInfo().getPhoneNumber()).isEqualTo("010-5555-6666");
	}

	@Test
	@DisplayName("배송 생성 시 배송 상세 정보가 올바르게 설정된다")
	void createShipping_withCorrectDetails() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		Rental savedRental = rentalRepository.save(RentalBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct)
			.build());

		Address address = new Address("11111", "서울시 종로구 세종대로 175", "정부서울청사");
		CreateShippingCommand command = CreateShippingCommand.of(
			Direction.OUTBOUND,
			"박민수",
			"010-7777-8888",
			address
		);

		// when
		Shipping shipping = shippingService.createShipping(savedMember, savedRental, command);

		// then
		assertThat(shipping.getShippingInfo().getAddress().getPostcode()).isEqualTo("11111");
		assertThat(shipping.getShippingInfo().getAddress().getPostAddress()).isEqualTo("서울시 종로구 세종대로 175");
		assertThat(shipping.getShippingInfo().getAddress().getDetailAddress()).isEqualTo("정부서울청사");
	}

	@Test
	@DisplayName("여러 배송을 생성해도 각각 고유한 운송장 번호가 생성된다")
	void createMultipleShippings_uniqueTrackingNumbers() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct1 = productRepository.save(ProductBuilder.create());
		Product savedProduct2 = productRepository.save(ProductBuilder.builder().withName("Product2").build());

		Rental savedRental1 = rentalRepository.save(RentalBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct1)
			.build());
		Rental savedRental2 = rentalRepository.save(RentalBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct2)
			.build());

		CreateShippingCommand command1 = CreateShippingCommand.of(
			Direction.OUTBOUND,
			"사용자1",
			"010-1111-1111",
			new Address("12345", "주소1", "상세주소1")
		);

		CreateShippingCommand command2 = CreateShippingCommand.of(
			Direction.OUTBOUND,
			"사용자2",
			"010-2222-2222",
			new Address("54321", "주소2", "상세주소2")
		);

		// when
		Shipping shipping1 = shippingService.createShipping(savedMember, savedRental1, command1);
		Shipping shipping2 = shippingService.createShipping(savedMember, savedRental2, command2);

		// then
		assertThat(shipping1.getTrackingNumber()).isNotEqualTo(shipping2.getTrackingNumber());
		assertThat(shipping1.getTrackingNumber()).matches("^\\d{20}$");
		assertThat(shipping2.getTrackingNumber()).matches("^\\d{20}$");
	}

	@Test
	@DisplayName("배송 시작 시 배송 상태가 변경되고 배송 시작 시간이 생성된다")
	void startShipping_success() {
		// given
		Member member = memberRepository.save(MemberBuilder.create());
		Product product = productRepository.save(ProductBuilder.create());
		Rental rental = rentalRepository.save(
			RentalBuilder.builder()
				.withProduct(product)
				.withMember(member)
				.build()
		);
		Shipping shipping = shippingRepository.save(
			ShippingBuilder.builder()
				.withRental(rental)
				.build()
		);
		flushAndClear();

		// when
		shippingService.startShipping(shipping.getId());

		// then
		Shipping savedShipping = shippingRepository.findById(shipping.getId()).orElseThrow();
		assertThat(savedShipping.getStatus()).isEqualTo(ShippingStatus.IN_DELIVERY);
		assertThat(savedShipping.getDetail().getStartAt()).isNotNull();
	}

	@Test
	@DisplayName("배송 대기 중 상태가 아닐 때 배송 시작을 하는 경우 예외가 발생한다")
	void startShipping_notPending() {
		// given
		Member member = memberRepository.save(MemberBuilder.create());
		Product product = productRepository.save(ProductBuilder.create());
		Rental rental = rentalRepository.save(
			RentalBuilder.builder()
				.withProduct(product)
				.withMember(member)
				.build()
		);
		Shipping shipping = shippingRepository.save(
			ShippingBuilder.builder()
				.withRental(rental)
				.build()
		);
		shipping.startShipping();
		flushAndClear();

		// when & then
		Shipping savedShipping = shippingRepository.findById(shipping.getId()).orElseThrow();
		assertThatThrownBy(() -> shippingService.startShipping(savedShipping.getId()))
			.isInstanceOf(DomainException.class)
			.hasMessage(ShippingError.NOT_PENDING.getMessage());
	}

	@Test
	@DisplayName("배송 완료 시 배송 상태가 변경되고 배송 완료 시간이 생성된다")
	void completeShipping_success() {
		// given
		Member member = memberRepository.save(MemberBuilder.create());
		Product product = productRepository.save(ProductBuilder.create());
		Rental rental = rentalRepository.save(
			RentalBuilder.builder()
				.withProduct(product)
				.withMember(member)
				.build()
		);
		Shipping shipping = shippingRepository.save(
			ShippingBuilder.builder()
				.withRental(rental)
				.build()
		);
		shipping.startShipping();
		flushAndClear();

		// when
		Shipping savedShipping = shippingRepository.findById(shipping.getId()).orElseThrow();
		shippingService.completeShipping(savedShipping.getId());

		// then
		assertThat(savedShipping.getStatus()).isEqualTo(ShippingStatus.DELIVERED);
		assertThat(savedShipping.getDetail().getEndAt()).isNotNull();
	}

	@Test
	@DisplayName("배송 완료 시 배송 방향이 Outbound인 경우 관련된 대여의 상태가 활성화 된다")
	void completeShipping_rentalStatusActive() {
		// given
		Member member = memberRepository.save(MemberBuilder.create());
		Product product = productRepository.save(ProductBuilder.create());
		Rental rental = rentalRepository.save(
			RentalBuilder.builder()
				.withProduct(product)
				.withMember(member)
				.build()
		);
		Shipping shipping = shippingRepository.save(
			ShippingBuilder.builder()
				.withRental(rental)
				.withDirection(Direction.OUTBOUND)
				.build()
		);
		shipping.startShipping();
		flushAndClear();

		// when
		shippingService.completeShipping(shipping.getId());

		// then
		Shipping savedShipping = shippingRepository.findById(shipping.getId()).orElseThrow();
		assertThat(savedShipping.getStatus()).isEqualTo(ShippingStatus.DELIVERED);
		assertThat(savedShipping.getDetail().getEndAt()).isNotNull();
		assertThat(savedShipping.getRental().getRentalStatus()).isEqualTo(RentalStatus.ACTIVE);
	}

	@Test
	@DisplayName("배송 중 상태가 아닐 때 배송 완료를 하는 경우 예외가 발생한다 ")
	void completeShipping_notInDelivery() {
		// given
		Member member = memberRepository.save(MemberBuilder.create());
		Product product = productRepository.save(ProductBuilder.create());
		Rental rental = rentalRepository.save(
			RentalBuilder.builder()
				.withProduct(product)
				.withMember(member)
				.build()
		);
		Shipping shipping = shippingRepository.save(
			ShippingBuilder.builder()
				.withRental(rental)
				.build()
		);
		shipping.startShipping();
		shipping.completeShipping();
		flushAndClear();

		// when & then
		Shipping savedShipping = shippingRepository.findById(shipping.getId()).orElseThrow();
		assertThatThrownBy(() -> shippingService.completeShipping(savedShipping.getId()))
			.isInstanceOf(DomainException.class)
			.hasMessage(ShippingError.NOT_IN_DELIVERY.getMessage());
	}

	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}