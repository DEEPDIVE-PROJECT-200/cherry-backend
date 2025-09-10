package ok.cherry.rental.application;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.status.RentalStatus;
import ok.cherry.rental.infrastructure.RentalRepository;
import ok.cherry.shipping.application.ShippingService;
import ok.cherry.shipping.application.command.CreateShippingCommand;
import ok.cherry.shipping.domain.Shipping;
import ok.cherry.shipping.domain.type.Direction;
import ok.cherry.shipping.exception.ShippingError;
import ok.cherry.shipping.infrastructure.ShippingRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalSchedulerService {

	private final RentalRepository rentalRepository;
	private final ShippingRepository shippingRepository;
	private final ShippingService shippingService;

	@Scheduled(cron = "0 0 0 * * *") // 매일 자정 실행
	public void processExpiredRentals() {
		LocalDate today = LocalDate.now();

		List<Rental> expiredRentals = rentalRepository.findByRentalStatusAndDetail_EndAtBefore(RentalStatus.ACTIVE, today);

		if (expiredRentals.isEmpty()) {
			return;
		}

		for (Rental rental : expiredRentals) {
			rental.inReturn();

			Shipping shipping = shippingRepository.findByRentalIdAndDirection(rental.getId(), Direction.OUTBOUND)
				.orElseThrow(() -> new BusinessException(ShippingError.SHIPPING_NOT_FOUND));

			CreateShippingCommand createShippingCommand = CreateShippingCommand.of(
				Direction.INBOUND,
				shipping.getShippingInfo().getReceiver(),
				shipping.getShippingInfo().getPhoneNumber(),
				shipping.getShippingInfo().getAddress()
			);
			shippingService.createShipping(rental.getMember(), rental, createShippingCommand);
		}
	}
}