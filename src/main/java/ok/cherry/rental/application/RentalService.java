package ok.cherry.rental.application;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.domain.Member;
import ok.cherry.rental.application.dto.request.RentalCreateRequest;
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

	public Rental createRental(Member member, RentalCreateRequest request) {
		validateRentalItems(request.items());
		validateRentalPeriod(request.rentStartAt(), request.rentEndAt());

		Rental rental = Rental.create(
			member,
			request.items(),
			RentalNumberGenerator.generate(),
			request.rentStartAt(),
			request.rentEndAt()
		);

		return rentalRepository.save(rental);
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
