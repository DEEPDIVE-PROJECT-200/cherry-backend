package ok.cherry.rental.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class RentalDetail {

	@Column(nullable = false)
	private LocalDateTime createAt;

	@Column(nullable = false)
	private LocalDateTime startAt;

	@Column(nullable = false)
	private LocalDateTime endAt;

	static RentalDetail create(LocalDateTime startAt, LocalDateTime endAt) {
		RentalDetail rentalDetail = new RentalDetail();
		rentalDetail.createAt = LocalDateTime.now();
		rentalDetail.startAt = startAt;
		rentalDetail.endAt = endAt;
		return rentalDetail;
	}
}