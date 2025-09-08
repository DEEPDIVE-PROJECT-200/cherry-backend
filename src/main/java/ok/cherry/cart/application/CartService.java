package ok.cherry.cart.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.cart.application.dto.request.CartCreateRequest;
import ok.cherry.cart.application.dto.request.CartDeleteRequest;
import ok.cherry.cart.application.dto.response.CartCreateResponse;
import ok.cherry.cart.application.dto.response.CartGetResponse;
import ok.cherry.cart.application.dto.response.CartInfoResponse;
import ok.cherry.cart.domain.Cart;
import ok.cherry.cart.exception.CartError;
import ok.cherry.cart.infrastructure.CartRepository;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.domain.Member;
import ok.cherry.member.exception.MemberError;
import ok.cherry.member.infrastructure.MemberRepository;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.type.Color;
import ok.cherry.product.exception.ProductError;
import ok.cherry.product.infrastructure.ProductRepository;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;
	private final MemberRepository memberRepository;
	private final ProductRepository productRepository;

	public CartCreateResponse createCart(CartCreateRequest request, String providerId) {
		Member member = memberRepository.findByProviderId(providerId)
			.orElseThrow(() -> new BusinessException(MemberError.USER_NOT_FOUND));

		Product product = productRepository.findById(request.productId())
			.orElseThrow(() -> new BusinessException(ProductError.PRODUCT_NOT_FOUND));

		validateCart(member.getId(), product.getId(), request.color());

		Cart cart = Cart.create(
			member,
			product,
			product.getDailyRentalPrice(),
			request.color()
		);

		Cart savedCart = cartRepository.save(cart);

		return CartCreateResponse.of(savedCart.getId());
	}

	public void deleteCart(CartDeleteRequest request, String providerId) {
		List<Cart> carts = cartRepository.findAllById(request.cartIds());

		if (carts.size() != request.cartIds().size()) {
			throw new BusinessException(CartError.CART_NOT_FOUND);
		}
		carts.forEach(cart -> validateCartsOwnership(List.of(cart), providerId));

		cartRepository.deleteAllInBatch(carts);
	}

	@Transactional(readOnly = true)
	public CartGetResponse getCarts(String providerId) {
		Member member = memberRepository.findByProviderId(providerId)
			.orElseThrow(() -> new BusinessException(MemberError.USER_NOT_FOUND));

		List<Cart> carts = cartRepository.findAllByMemberIdWithProduct(member.getId());

		List<CartInfoResponse> cartInfoResponses = new ArrayList<>();
		BigDecimal totalPrice = BigDecimal.ZERO;

		for (Cart cart : carts) {
			cartInfoResponses.add(new CartInfoResponse(
				cart.getId(),
				cart.getProduct().getId(),
				cart.getProduct().getName(),
				cart.getProduct().getThumbnailUrl(),
				cart.getColor(),
				cart.getPrice()
			));
			totalPrice = totalPrice.add(cart.getPrice());
		}

		return new CartGetResponse(cartInfoResponses, totalPrice);
	}

	@Transactional(readOnly = true)
	public List<CartInfoResponse> getCartsByIds(List<Long> cartIds, String providerId) {
		List<Cart> carts = cartRepository.findAllById(cartIds);

		if (carts.size() != cartIds.size()) {
			log.info("요청한 cartIds 개수: {}, 조회된 cart 개수: {}", cartIds.size(), carts.size());
			throw new BusinessException(CartError.CART_NOT_FOUND);
		}

		validateCartsOwnership(carts, providerId);

		return carts.stream()
			.map(cart -> new CartInfoResponse(
				cart.getId(),
				cart.getProduct().getId(),
				cart.getProduct().getName(),
				cart.getProduct().getThumbnailUrl(),
				cart.getColor(),
				cart.getPrice()
			))
			.toList();
	}

	private static void validateCartsOwnership(List<Cart> carts, String providerId) {
		boolean hasUnauthorizedCart = carts.stream()
			.anyMatch(cart -> !cart.getMember().getProviderId().equals(providerId));

		if (hasUnauthorizedCart) {
			log.info("권한 없는 장바구니 접근 시도 - providerId: {}", providerId);
			throw new BusinessException(CartError.UNAUTHORIZED_CART_ACCESS);
		}
	}

	private void validateCart(Long memberId, Long productId, Color color) {
		List<Cart> memberCarts = cartRepository.findAllByMemberId(memberId);

		if (memberCarts.size() >= 3) {
			throw new BusinessException(CartError.CART_LIMIT_EXCEEDED);
		}

		boolean isDuplicate = memberCarts.stream()
			.anyMatch(cart -> cart.getProduct().getId().equals(productId) && cart.getColor().equals(color));
		if (isDuplicate) {
			throw new BusinessException(CartError.DUPLICATE_CART);
		}
	}
}
