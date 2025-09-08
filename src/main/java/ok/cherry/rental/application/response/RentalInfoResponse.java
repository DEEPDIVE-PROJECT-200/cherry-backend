package ok.cherry.rental.application.response;

import java.time.LocalDate;
import java.util.List;

import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.status.RentalStatus;

public record RentalInfoResponse(
	Long rentalId,
	String rentalNumber,
	RentalStatus status,
	LocalDate startAt,
	LocalDate endAt,
	List<RentalItemInfoResponse> items
) {
	public static RentalInfoResponse from(Rental rental) {
		List<RentalItemInfoResponse> items = rental.getRentalItems().stream()
			.map(RentalItemInfoResponse::from)
			.toList();

		return new RentalInfoResponse(
			rental.getId(),
			rental.getRentalNumber(),
			rental.getRentalStatus(),
			rental.getDetail().getStartAt(),
			rental.getDetail().getEndAt(),
			items
		);
	}
}
