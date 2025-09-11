package ok.cherry.shipping.application.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TrackingNumberResponse(
	String trackingNumber,
	LocalDate startAt
) {
	public static TrackingNumberResponse of(String trackingNumber, LocalDateTime startAt) {
		return new TrackingNumberResponse(trackingNumber, startAt.toLocalDate());
	}
}
