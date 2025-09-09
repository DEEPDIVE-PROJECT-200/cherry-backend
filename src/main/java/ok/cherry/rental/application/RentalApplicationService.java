package ok.cherry.rental.application;

import static java.time.temporal.ChronoUnit.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.cart.application.CartService;
import ok.cherry.cart.application.dto.request.CartDeleteRequest;
import ok.cherry.cart.application.dto.response.CartInfoResponse;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.application.MemberService;
import ok.cherry.member.domain.Member;
import ok.cherry.payment.application.PaymentService;
import ok.cherry.payment.application.dto.command.CreatePaymentCommand;
import ok.cherry.payment.domain.Payment;
import ok.cherry.product.application.ProductQueryService;
import ok.cherry.product.domain.Product;
import ok.cherry.rental.application.command.CreateRentalCommand;
import ok.cherry.rental.application.request.PlaceRentalOrderRequest;
import ok.cherry.rental.application.response.PlaceRentalOrderResponse;
import ok.cherry.rental.application.response.RentalCompleteResponse;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.RentalItem;
import ok.cherry.rental.exception.RentalError;
import ok.cherry.shipping.application.ShippingService;
import ok.cherry.shipping.application.command.CreateShippingCommand;
import ok.cherry.shipping.domain.Shipping;
import ok.cherry.shipping.domain.type.Direction;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RentalApplicationService {

	private static final BigDecimal DEFAULT_SHIPPING_FEE = new BigDecimal("0");
	private static final BigDecimal DEFAULT_CLEANING_FEE = new BigDecimal("0");

	private final RentalService rentalService;
	private final MemberService memberService;
	private final CartService cartService;
	private final PaymentService paymentService;
	private final ShippingService shippingService;
	private final ProductQueryService productService;

	public PlaceRentalOrderResponse placeRentalOrder(String providerId, PlaceRentalOrderRequest request) {
		validateRentalOrderRequest(request);

		Member member = memberService.findMemberByProviderId(providerId);
		List<RentalItem> rentalItems = prepareRentalItems(request, providerId);

		Rental rental = createRental(request, rentalItems, member);
		Payment payment = processPayment(request, member, rental);
		Shipping shipping = createShipping(request, member, rental);

		cleanupCartIfNeeded(providerId, request);

		log.info("대여 주문 생성 완료 - 대여 ID: {}, 회원 ID: {}", rental.getId(), member.getId());

		return PlaceRentalOrderResponse.of(
			rental.getId(),
			rental.getRentalNumber(),
			payment.getId(),
			shipping.getId(),
			payment.getPaymentAmount().getTotalAmount()
		);
	}

	public RentalCompleteResponse completeRental(Long rentalId) {
		Rental rental = rentalService.findRentalById(rentalId);
		validateReturnShippingCompleted(rentalId);
		rental.complete();

		log.info("검수 완료 처리 - 대여 ID: {}", rentalId);
		return RentalCompleteResponse.of(rentalId, rental.getRentalStatus());
	}

	private void validateReturnShippingCompleted(Long rentalId) {
		shippingService.validateReturnShippingCompleted(rentalId);
	}

	private static void validateRentalOrderRequest(PlaceRentalOrderRequest request) {
		if (!request.isDirectRental() && !request.isCartRental()) {
			throw new BusinessException(RentalError.INVALID_RENTAL_REQUEST);
		}

		if (request.isDirectRental() && request.isCartRental()) {
			throw new BusinessException(RentalError.AMBIGUOUS_RENTAL_REQUEST);
		}
	}

	private List<RentalItem> prepareRentalItems(PlaceRentalOrderRequest request, String providerId) {
		if (request.isDirectRental()) {
			return prepareDirectRentalItems(request);
		} else {
			return prepareCartRentalItems(request, providerId);
		}
	}

	private List<RentalItem> prepareDirectRentalItems(PlaceRentalOrderRequest request) {
		Product product = productService.getProductById(request.productId());

		long rentalDays = calculateRentalDays(request.rentStartAt(), request.rentEndAt());
		BigDecimal totalPrice = product.getDailyRentalPrice().multiply(BigDecimal.valueOf(rentalDays));

		RentalItem rentalItem = RentalItem.create(product, totalPrice, request.color());
		return List.of(rentalItem);
	}

	private List<RentalItem> prepareCartRentalItems(PlaceRentalOrderRequest request, String providerId) {
		List<CartInfoResponse> cartInfos = cartService.getCartsByIds(request.cartIds(), providerId);
		long rentalDays = calculateRentalDays(request.rentStartAt(), request.rentEndAt());

		return cartInfos.stream()
			.map(cartInfo -> {
				Product product = productService.getProductById(cartInfo.productId());
				BigDecimal totalPrice = cartInfo.dailyRentalPrice().multiply(BigDecimal.valueOf(rentalDays));
				return RentalItem.create(product, totalPrice, cartInfo.color());
			})
			.toList();
	}

	private long calculateRentalDays(LocalDate startAt, LocalDate endAt) {
		return DAYS.between(startAt, endAt) + 1;
	}

	private Rental createRental(PlaceRentalOrderRequest request, List<RentalItem> rentalItems, Member member) {
		CreateRentalCommand createRentalCommand = CreateRentalCommand.of(
			rentalItems,
			request.rentStartAt(),
			request.rentEndAt()
		);
		return rentalService.createRental(member, createRentalCommand);
	}

	private Payment processPayment(PlaceRentalOrderRequest request, Member member, Rental rental) {
		CreatePaymentCommand createPaymentCommand = CreatePaymentCommand.of(
			request.paymentMethod(),
			DEFAULT_SHIPPING_FEE,
			DEFAULT_CLEANING_FEE
		);
		return paymentService.createPayment(member, rental, createPaymentCommand);
	}

	private Shipping createShipping(PlaceRentalOrderRequest request, Member member, Rental rental) {
		CreateShippingCommand createShippingCommand = CreateShippingCommand.of(
			Direction.OUTBOUND,
			request.shippingInfo().receiver(),
			request.shippingInfo().phoneNumber(),
			request.shippingInfo().address().toDomain()
		);
		return shippingService.createShipping(member, rental, createShippingCommand);
	}

	private void cleanupCartIfNeeded(String providerId, PlaceRentalOrderRequest request) {
		if (request.isCartRental()) {
			cartService.deleteCart(CartDeleteRequest.of(request.cartIds()), providerId);
		}
	}
}
