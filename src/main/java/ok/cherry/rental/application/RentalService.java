package ok.cherry.rental.application;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.domain.Member;
import ok.cherry.rental.application.command.CreateRentalCommand;
import ok.cherry.rental.application.response.RentalGetResponse;
import ok.cherry.rental.application.response.RentalInfoResponse;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.RentalItem;
import ok.cherry.rental.exception.RentalError;
import ok.cherry.rental.infrastructure.RentalRepository;
import ok.cherry.rental.util.RentalNumberGenerator;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RentalService {

	private final RentalRepository rentalRepository;

	public Rental createRental(Member member, CreateRentalCommand command) {
		validateRentalItems(command.items());
		validateRentalPeriod(command.rentStartAt(), command.rentEndAt());

		Rental rental = Rental.create(
			member,
			command.items(),
			RentalNumberGenerator.generate(),
			command.rentStartAt(),
			command.rentEndAt()
		);

		return rentalRepository.save(rental);
	}

	@Transactional(readOnly = true)
	public RentalGetResponse getRentals(Long lastRentalId, int limit, String providerId) {
		Pageable pageable = PageRequest.of(0, limit + 1);
		
		List<Rental> rentals = rentalRepository.findRentalsWithCursorPagination(
			providerId, lastRentalId, pageable
		);

		boolean hasNext = rentals.size() > limit;
		if (hasNext) {
			rentals.remove(limit);
		}

		List<RentalInfoResponse> rentalInfoResponses = rentals.stream()
			.map(RentalInfoResponse::from)
			.toList();

		Long lastId = null;
		if (!rentals.isEmpty()) {
			lastId = rentals.getLast().getId();
		}

		return new RentalGetResponse(rentalInfoResponses, hasNext, lastId);
	}

	public void completeReview(Long rentalId) {
		Rental rental = rentalRepository.findById(rentalId)
			.orElseThrow(() -> new BusinessException(RentalError.RENTAL_NOT_FOUND));

		rental.completeReview();
	}

	@Transactional(readOnly = true)
	public Rental findRentalById(Long rentalId) {
		return rentalRepository.findById(rentalId)
			.orElseThrow(() -> new BusinessException(RentalError.RENTAL_NOT_FOUND));
	}

	private static void validateRentalItems(List<RentalItem> items) {
		if (items.isEmpty()) {
			throw new BusinessException(RentalError.RENTAL_ITEMS_NOT_EMPTY);
		}
	}

	private static void validateRentalPeriod(LocalDate rentStartAt, LocalDate rentEndAt) {
		if (rentStartAt.isAfter(rentEndAt)) {
			throw new BusinessException(RentalError.INVALID_RENTAL_PERIOD);
		}
	}
}
