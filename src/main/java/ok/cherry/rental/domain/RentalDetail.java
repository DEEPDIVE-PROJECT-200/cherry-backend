package ok.cherry.rental.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalDetail {

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime startAt;

	@Column(nullable = false)
	private LocalDateTime endAt;

	static RentalDetail create(LocalDateTime startAt, LocalDateTime endAt) {
		RentalDetail rentalDetail = new RentalDetail();
		rentalDetail.createdAt = LocalDateTime.now();
		rentalDetail.startAt = startAt;
		rentalDetail.endAt = endAt;
		return rentalDetail;
	}
}