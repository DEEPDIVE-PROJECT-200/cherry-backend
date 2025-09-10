package ok.cherry.rental.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.member.infrastructure.MemberRepository;
import ok.cherry.product.ProductBuilder;
import ok.cherry.product.domain.Product;
import ok.cherry.product.infrastructure.ProductRepository;
import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.RentalItemBuilder;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.RentalItem;
import ok.cherry.rental.domain.status.RentalStatus;
import ok.cherry.rental.infrastructure.RentalRepository;
import ok.cherry.shipping.ShippingBuilder;
import ok.cherry.shipping.domain.Shipping;
import ok.cherry.shipping.domain.type.Direction;
import ok.cherry.shipping.infrastructure.ShippingRepository;

@SpringBootTest
@Transactional
class RentalSchedulerServiceTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private RentalRepository rentalRepository;

	@Autowired
	private ShippingRepository shippingRepository;

	@Autowired
	private RentalSchedulerService rentalSchedulerService;

	@Test
	@DisplayName("만료된 대여가 있을 때, 상태가 변경되고 배송 객체가 생성된다")
	void processExpiredRentals_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		RentalItem rentalItem1 = RentalItemBuilder.builder().withProduct(savedProduct).build();
		RentalItem rentalItem2 = RentalItemBuilder.builder().withProduct(savedProduct).build();

		LocalDate today = LocalDate.now();
		Rental expiredRental = RentalBuilder.builder()
			.withMember(savedMember)
			.withRentalItems(List.of(rentalItem1))
			.withStartAt(today.minusDays(8))
			.withEndAt(today.minusDays(1)) // 대여 기간이 종료되어 기간이 지나면 자동 삭제
			.build();
		expiredRental.active();

		Rental activeRental = RentalBuilder.builder()
			.withMember(savedMember)
			.withRentalItems(List.of(rentalItem2))
			.build();
		activeRental.active();
		rentalRepository.saveAll(List.of(expiredRental, activeRental));

		Shipping expiredRentalshipping = shippingRepository.save(
			ShippingBuilder.builder()
				.withRental(expiredRental)
				.build()
		);

		// when
		rentalSchedulerService.processExpiredRentals();

		// then
		Rental foundExpiredRental = rentalRepository.findById(expiredRental.getId()).orElseThrow();
		assertThat(foundExpiredRental.getRentalStatus()).isEqualTo(RentalStatus.IN_RETURN);

		// 만료되지 않은 렌탈 상태는 그대로 ACTIVE 인지 검증
		Rental foundActiveRental = rentalRepository.findById(activeRental.getId()).orElseThrow();
		assertThat(foundActiveRental.getRentalStatus()).isEqualTo(RentalStatus.ACTIVE);

		// 만료된 대여의 OUTBOUND 배송 정보로 INBOUND 배송이 만들어졌는지 검증
		Shipping savedReturnShipping = shippingRepository.findByRentalIdAndDirection(expiredRental.getId(), Direction.INBOUND).orElseThrow();
		assertThat(savedReturnShipping.getDirection()).isEqualTo(Direction.INBOUND);
		assertThat(savedReturnShipping.getShippingInfo().getReceiver()).isEqualTo(
			expiredRentalshipping.getShippingInfo().getReceiver());
		assertThat(savedReturnShipping.getShippingInfo().getPhoneNumber()).isEqualTo(
			expiredRentalshipping.getShippingInfo().getPhoneNumber());
		assertThat(savedReturnShipping.getShippingInfo().getAddress()).isEqualTo(
			expiredRentalshipping.getShippingInfo().getAddress());
	}

	@Test
	@DisplayName("만료된 대여가 없을 때, 아무런 작업도 수행되지 않아야 한다")
	void processExpiredRentals_success_no_action() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		RentalItem rentalItem = RentalItemBuilder.builder().withProduct(savedProduct).build();
		Rental activeRental = RentalBuilder.builder()
			.withMember(savedMember)
			.withRentalItems(List.of(rentalItem))
			.build();
		activeRental.active();
		rentalRepository.save(activeRental);

		// when
		rentalSchedulerService.processExpiredRentals();

		// then
		Rental foundRental = rentalRepository.findById(activeRental.getId()).orElseThrow();
		assertThat(foundRental.getRentalStatus()).isEqualTo(RentalStatus.ACTIVE);

		// 생성된 배송 객체가 없음을 검증
		assertThat(shippingRepository.findByRentalId(activeRental.getId())).isEmpty();
	}
}