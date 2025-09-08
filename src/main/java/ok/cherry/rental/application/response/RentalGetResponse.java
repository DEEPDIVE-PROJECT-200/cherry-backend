package ok.cherry.rental.application.response;

import java.util.List;

public record RentalGetResponse(
	List<RentalInfoResponse> rentals,
	boolean hasNext,
	Long lastRentalId
) {
}
