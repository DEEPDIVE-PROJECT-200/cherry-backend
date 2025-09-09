package ok.cherry.shipping.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.domain.Member;
import ok.cherry.rental.domain.Rental;
import ok.cherry.shipping.application.command.CreateShippingCommand;
import ok.cherry.shipping.domain.Shipping;
import ok.cherry.shipping.domain.type.Direction;
import ok.cherry.shipping.exception.ShippingError;
import ok.cherry.shipping.infrastructure.ShippingRepository;
import ok.cherry.shipping.util.TrackingNumberGenerator;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShippingService {

	private final ShippingRepository shippingRepository;

	@Transactional
	public Shipping createShipping(
		Member member,
		Rental rental,
		CreateShippingCommand shippingCommand
	) {
		Shipping shipping = Shipping.create(
			member,
			rental,
			TrackingNumberGenerator.generate(),
			shippingCommand.direction(),
			shippingCommand.receiver(),
			shippingCommand.phoneNumber(),
			shippingCommand.address()
		);
		callExternalShippingGateway(shipping);

		return shippingRepository.save(shipping);
	}

	@Transactional
	public void startShipping(Long shippingId) {
		Shipping shipping = shippingRepository.findById(shippingId)
			.orElseThrow(() -> new BusinessException(ShippingError.SHIPPING_NOT_FOUND));
		shipping.startShipping();
		log.info("배송 시작 - 운송장 번호:{}, 배송 시작 시간: {}", shipping.getTrackingNumber(), shipping.getDetail().getStartAt());
	}

	@Transactional
	public void completeShipping(Long shippingId) {
		Shipping shipping = shippingRepository.findById(shippingId)
			.orElseThrow(() -> new BusinessException(ShippingError.SHIPPING_NOT_FOUND));

		if (shipping.getDirection() == Direction.OUTBOUND) {
			shipping.getRental().active();
		}
		shipping.completeShipping();
		log.info("배송 완료 - 운송장 번호:{}, 배송 완료 시간: {}", shipping.getTrackingNumber(), shipping.getDetail().getEndAt());
	}

	/**
	 * 외부 배송사 API 호출 로직
	 * 현재는 단순화하여 항상 성공으로 처리
	 * @param shipping
	 */
	private void callExternalShippingGateway(Shipping shipping) {
		log.info("외부 배송사 배송 요청 처리 완료 - 운송장 번호: {}", shipping.getTrackingNumber());
	}
}
