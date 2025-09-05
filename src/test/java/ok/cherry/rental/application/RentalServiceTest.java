package ok.cherry.rental.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.member.infrastructure.MemberRepository;
import ok.cherry.product.ProductBuilder;
import ok.cherry.product.domain.Product;
import ok.cherry.product.infrastructure.ProductRepository;
import ok.cherry.rental.RentalItemBuilder;
import ok.cherry.rental.application.command.CreateRentalCommand;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.RentalItem;
import ok.cherry.rental.domain.status.RentalStatus;
import ok.cherry.rental.exception.RentalError;
import ok.cherry.rental.infrastructure.RentalRepository;

@SpringBootTest
@Transactional
class RentalServiceTest {

	@Autowired
	RentalService rentalService;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	RentalRepository rentalRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	EntityManager entityManager;

	@Test
	@DisplayName("대여 생성에 성공한다")
	void createRental_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		RentalItem rentalItem = RentalItemBuilder.builder().withProduct(savedProduct).build();
		LocalDate startAt = LocalDate.now();
		LocalDate endAt = startAt.plusDays(7);
		CreateRentalCommand command = new CreateRentalCommand(List.of(rentalItem), startAt, endAt);

		// when
		Rental rental = rentalService.createRental(savedMember, command);
		flushAndClear();

		// then
		Rental savedRental = rentalRepository.findById(rental.getId()).orElseThrow();
		assertThat(savedRental.getMember().getId()).isEqualTo(savedMember.getId());
		assertThat(savedRental.getRentalStatus()).isEqualTo(RentalStatus.PENDING);
		assertThat(savedRental.getRentalItems()).hasSize(1);
		assertThat(savedRental.getRentalItems().getFirst().getId()).isEqualTo(rentalItem.getId());
	}

	@Test
	@DisplayName("여러 대여 아이템으로 대여 생성에 성공한다")
	void createRental_withMultipleItems_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product product1 = productRepository.save(ProductBuilder.create());
		Product product2 = productRepository.save(ProductBuilder.builder().withName("product2").build());
		RentalItem rentalItem1 = RentalItemBuilder.builder().withProduct(product1).build();
		RentalItem rentalItem2 = RentalItemBuilder.builder().withProduct(product2).build();
		LocalDate startAt = LocalDate.now();
		LocalDate endAt = startAt.plusDays(7);
		CreateRentalCommand command = new CreateRentalCommand(List.of(rentalItem1, rentalItem2), startAt, endAt);

		// when
		Rental rental = rentalService.createRental(savedMember, command);
		flushAndClear();

		// then
		Rental savedRental = rentalRepository.findById(rental.getId()).orElseThrow();
		assertThat(savedRental.getMember().getId()).isEqualTo(savedMember.getId());
		assertThat(savedRental.getRentalStatus()).isEqualTo(RentalStatus.PENDING);
		assertThat(savedRental.getRentalItems()).hasSize(2);
		assertThat(savedRental.getTotalPrice()).isEqualByComparingTo(rentalItem1.getPrice().add(rentalItem2.getPrice()));
	}

	@Test
	@DisplayName("빈 대여 아이템 리스트로 대여 생성 시 예외가 발생한다")
	void createRental_withEmptyItems_throwsException() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		LocalDate startAt = LocalDate.now();
		LocalDate endAt = startAt.plusDays(7);
		CreateRentalCommand command = new CreateRentalCommand(List.of(), startAt, endAt);

		// when & then
		assertThatThrownBy(() -> rentalService.createRental(savedMember, command))
			.isInstanceOf(BusinessException.class)
			.hasMessage(RentalError.RENTAL_ITEMS_NOT_EMPTY.getMessage());
	}

	@Test
	@DisplayName("시작일이 종료일보다 늦은 경우 예외가 발생한다")
	void createRental_withInvalidDateRange_throwsException() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		RentalItem rentalItem = RentalItemBuilder.builder().withProduct(savedProduct).build();
		LocalDate startAt = LocalDate.now().plusDays(7);
		LocalDate endAt = LocalDate.now();
		CreateRentalCommand command = new CreateRentalCommand(List.of(rentalItem), startAt, endAt);

		// when & then
		assertThatThrownBy(() -> rentalService.createRental(savedMember, command))
			.isInstanceOf(BusinessException.class)
			.hasMessage(RentalError.INVALID_RENTAL_PERIOD.getMessage());
	}

	@Test
	@DisplayName("대여 번호가 올바른 형식으로 생성된다")
	void createRental_generateValidRentalNumber() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		RentalItem rentalItem = RentalItemBuilder.builder().withProduct(savedProduct).build();
		LocalDate startAt = LocalDate.now();
		LocalDate endAt = startAt.plusDays(7);
		CreateRentalCommand command = new CreateRentalCommand(List.of(rentalItem), startAt, endAt);

		// when
		Rental rental = rentalService.createRental(savedMember, command);
		flushAndClear();

		// then
		Rental savedRental = rentalRepository.findById(rental.getId()).orElseThrow();
		assertThat(savedRental.getRentalNumber()).matches("^CH-\\d{20}$");
	}

	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}