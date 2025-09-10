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
import ok.cherry.global.exception.error.DomainException;
import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.member.infrastructure.MemberRepository;
import ok.cherry.product.ProductBuilder;
import ok.cherry.product.domain.Product;
import ok.cherry.product.infrastructure.ProductRepository;
import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.RentalItemBuilder;
import ok.cherry.rental.application.command.CreateRentalCommand;
import ok.cherry.rental.application.response.RentalGetResponse;
import ok.cherry.rental.application.response.RentalInfoResponse;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.RentalItem;
import ok.cherry.rental.domain.status.RentalStatus;
import ok.cherry.rental.domain.status.ReviewStatus;
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

	@Test
	@DisplayName("요청을 보낸 사용자의 대여 목록이 커서 기반 페이지네이션으로 정상 동작한다")
	void getRentals_success_withCursorPagination() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		saveRentals(savedMember, savedProduct);

		// when
		RentalGetResponse firstPage = rentalService.getRentals(null, 2, savedMember.getProviderId());
		RentalGetResponse secondPage = rentalService.getRentals(firstPage.lastRentalId(), 2, savedMember.getProviderId());

		// then
		assertThat(firstPage.rentals()).hasSize(2);
		assertThat(firstPage.hasNext()).isTrue();
		assertThat(firstPage.lastRentalId()).isNotNull();

		assertThat(secondPage.rentals()).hasSize(2);
		assertThat(secondPage.hasNext()).isTrue();

		// 첫 번째와 두 번째 페이지의 데이터가 중복되지 않는지 확인
		List<Long> firstPageIds = firstPage.rentals().stream()
			.map(RentalInfoResponse::rentalId)
			.toList();
		List<Long> secondPageIds = secondPage.rentals().stream()
			.map(RentalInfoResponse::rentalId)
			.toList();
		assertThat(firstPageIds).doesNotContainAnyElementsOf(secondPageIds);
	}

	@Test
	@DisplayName("마지막 페이지에서는 hasNext가 false이다")
	void getRentals_success_lastPage_hasNextIsFalse() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product product = productRepository.save(ProductBuilder.create());

		Rental rental = RentalBuilder.builder()
			.withMember(savedMember)
			.withRentalItems(List.of(RentalItemBuilder.builder().withProduct(product).build()))
			.withRentalNumber("CH-25090213363012500001")
			.build();
		rentalRepository.save(rental);

		// when
		RentalGetResponse response = rentalService.getRentals(null, 2, savedMember.getProviderId());

		// then
		assertThat(response.rentals()).hasSize(1);
		assertThat(response.hasNext()).isFalse();
		assertThat(response.lastRentalId()).isEqualTo(rental.getId());
	}

	@Test
	@DisplayName("리뷰 작성 완료 시 리뷰 상태가 COMPLETED로 변경된다")
	void completeReview_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product product = productRepository.save(ProductBuilder.create());

		Rental rental = RentalBuilder.builder()
			.withMember(savedMember)
			.withRentalItems(List.of(RentalItemBuilder.builder().withProduct(product).build()))
			.build();
		rentalRepository.save(rental);
		rental.active();
		rental.inReturn();
		rental.complete();

		// when
		rentalService.completeReview(rental.getId());
		flushAndClear();

		// then
		Rental savedReview = rentalRepository.findById(rental.getId()).orElseThrow();
		assertThat(savedReview.getReviewStatus()).isEqualTo(ReviewStatus.COMPLETED);
	}

	@Test
	@DisplayName("리뷰 상태가 COMPLETE가 아니면 예외가 발생한다")
	void completeReview_notCompleted() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product product = productRepository.save(ProductBuilder.create());

		Rental rental = RentalBuilder.builder()
			.withMember(savedMember)
			.withRentalItems(List.of(RentalItemBuilder.builder().withProduct(product).build()))
			.build();
		rentalRepository.save(rental);

		// when & then
		assertThatThrownBy(() -> rentalService.completeReview(rental.getId()))
			.isInstanceOf(DomainException.class)
			.hasMessage(RentalError.NOT_COMPLETED.getMessage());
	}

	@Test
	@DisplayName("자신의 대여 기록을 상세 조회하는데 성공한다")
	void getRental_success() {
		// given
		Member member = memberRepository.save(MemberBuilder.create());
		Product product = productRepository.save(ProductBuilder.create());
		Rental rental = rentalRepository.save(RentalBuilder.builder()
			.withMember(member)
			.withRentalItems(List.of(RentalItemBuilder.builder().withProduct(product).build()))
			.build());

		// when
		Rental result = rentalService.getRental(rental.getId(), member.getProviderId());

		// then
		assertThat(result.getId()).isEqualTo(rental.getId());
		assertThat(result.getMember().getId()).isEqualTo(member.getId());
	}

	@Test
	@DisplayName("다른 사람의 대여 기록을 조회하면 예외가 발생한다")
	void getRental_forbidden() {
		// given
		Member owner = memberRepository.save(MemberBuilder.create());
		Member other = memberRepository.save(MemberBuilder.builder()
			.withProviderId("other")
			.withEmail("other@test.com")
			.withNickname("other")
			.build()
		);
		Product product = productRepository.save(ProductBuilder.create());
		Rental rental = rentalRepository.save(RentalBuilder.builder()
			.withMember(owner)
			.withRentalItems(List.of(RentalItemBuilder.builder().withProduct(product).build()))
			.build());

		// when & then
		assertThatThrownBy(() -> rentalService.getRental(rental.getId(), other.getProviderId()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(RentalError.FORBIDDEN_ACCESS.getMessage());
	}

	@Test
	@DisplayName("존재하지 않는 대여 기록을 조회하면 예외가 발생한다")
	void getRental_notFound() {
		// given
		Member member = memberRepository.save(MemberBuilder.create());
		long notExistRentalId = 999L;

		// when & then
		assertThatThrownBy(() -> rentalService.getRental(notExistRentalId, member.getProviderId()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(RentalError.RENTAL_NOT_FOUND.getMessage());
	}

	private void saveRentals(Member savedMember, Product product) {
		rentalRepository.saveAll(List.of(
			RentalBuilder.builder()
				.withMember(savedMember)
				.withRentalItems(List.of(RentalItemBuilder.builder().withProduct(product).build()))
				.withRentalNumber("CH-25090213363012300001")
				.build(),
			RentalBuilder.builder()
				.withMember(savedMember)
				.withRentalItems(List.of(RentalItemBuilder.builder().withProduct(product).build()))
				.withRentalNumber("CH-25090213363012300002")
				.build(),
			RentalBuilder.builder()
				.withMember(savedMember)
				.withRentalItems(List.of(RentalItemBuilder.builder().withProduct(product).build()))
				.withRentalNumber("CH-25090213363012300003")
				.build(),
			RentalBuilder.builder()
				.withMember(savedMember)
				.withRentalItems(List.of(RentalItemBuilder.builder().withProduct(product).build()))
				.withRentalNumber("CH-25090213363012300004")
				.build(),
			RentalBuilder.builder()
				.withMember(savedMember)
				.withRentalItems(List.of(RentalItemBuilder.builder().withProduct(product).build()))
				.withRentalNumber("CH-25090213363012300005")
				.build()
		));
	}

	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}