package ok.cherry.rental.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ok.cherry.cart.exception.CartError;
import ok.cherry.cart.infrastructure.CartRepository;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.domain.Member;
import ok.cherry.member.exception.MemberError;
import ok.cherry.member.infrastructure.MemberRepository;
import ok.cherry.product.domain.Product;
import ok.cherry.product.exception.ProductError;
import ok.cherry.product.infrastructure.ProductRepository;
import ok.cherry.rental.application.dto.request.CartRentalProcessRequest;
import ok.cherry.rental.application.dto.request.DirectRentalProcessRequest;
import ok.cherry.rental.application.dto.request.RentalCreateRequest;
import ok.cherry.rental.application.dto.response.RentalProcessResponse;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.RentalItem;
import ok.cherry.rental.infrastructure.RentalItemRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalApplicationService {

	private final MemberRepository memberRepository;
	private final RentalItemRepository rentalItemRepository;
	private final CartRepository cartRepository;
	private final ProductRepository productRepository;
	private final RentalService rentalService;

	// 단건 -> 장바구니 없이 결제하는 경우
	public RentalProcessResponse processRental(String providerId, DirectRentalProcessRequest directRentalProcessRequest) {
		Member member = getMember(providerId);

		// RentalCreateRequest 매핑
		RentalCreateRequest rentalCreateRequest = getRentalCreateRequest(directRentalProcessRequest);

		// PaymentCreateRequest 매핑

		// ShippingCreateRequest 매핑

		Rental rental = rentalService.createRental(member, rentalCreateRequest);
		// Payment payment = paymentService.createPayment(member, rental, paymentCreateRequest);
		// Shipping shipping = shippingService.createShipping(member, rental, shippingCreateRequest);
		return RentalProcessResponse.of();
	}


	// 여러건 -> 장바구니에서 결제하는 경우
	public RentalProcessResponse processRentals(String providerId, CartRentalProcessRequest cartRentalProcessRequest) {
		Member member = getMember(providerId);

		// RentalCreateRequest 매핑
		RentalCreateRequest rentalCreateRequest = getRentalCreateRequest(cartRentalProcessRequest);

		// PaymentCreateRequest 매핑

		// ShippingCreateRequest 매핑

		Rental rental = rentalService.createRental(member, rentalCreateRequest);
		// Payment payment = paymentService.createPayment(member, rental, paymentCreateRequest);
		// Shipping shipping = shippingService.createShipping(member, rental, shippingCreateRequest);
		return RentalProcessResponse.of();
	}

	private Member getMember(String providerId) {
		return memberRepository.findByProviderId(providerId)
			.orElseThrow(() -> new BusinessException(MemberError.USER_NOT_FOUND));
	}

	private RentalCreateRequest getRentalCreateRequest(DirectRentalProcessRequest request) {
		Product product = productRepository.findById(request.productId())
			.orElseThrow(() -> new BusinessException(ProductError.PRODUCT_NOT_FOUND));

		RentalItem rentalItem = rentalItemRepository.save(
			RentalItem.create(product, product.getDailyRentalPrice(), request.color())
		);

		return new RentalCreateRequest(List.of(rentalItem), request.rentStartAt(), request.rentEndAt());
	}

	private RentalCreateRequest getRentalCreateRequest(CartRentalProcessRequest cartRentalProcessRequest) {
		List<Long> cartIds = cartRentalProcessRequest.cartIds();

		List<RentalItem> rentalItems = cartIds.stream()
			.map(cartId -> cartRepository.findById(cartId)
				.orElseThrow(() -> new BusinessException(CartError.CART_NOT_FOUND)))
			.map(cart -> RentalItem.create(
				cart.getProduct(),
				cart.getProduct().getDailyRentalPrice(),
				cart.getColor()))
			.map(rentalItemRepository::save)
			.toList();

		return new RentalCreateRequest(rentalItems, cartRentalProcessRequest.rentStartAt(), cartRentalProcessRequest.rentEndAt());
	}
}
