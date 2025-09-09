package ok.cherry.rental.application.response;

import ok.cherry.rental.domain.status.RentalStatus;

public record RentalCompleteResponse(
	Long rentalId,
	RentalStatus status
) {
	public static RentalCompleteResponse of(Long rentalId, RentalStatus status) {
		return new RentalCompleteResponse(rentalId, status);
	}
}
