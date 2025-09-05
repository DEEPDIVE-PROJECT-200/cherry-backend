package ok.cherry.cart.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ok.cherry.cart.application.dto.request.CartAddRequest;
import ok.cherry.cart.application.dto.request.CartDeleteRequest;
import ok.cherry.cart.application.dto.response.CartAddResponse;
import ok.cherry.cart.domain.Cart;
import ok.cherry.cart.exception.CartError;
import ok.cherry.cart.infrastructure.CartRepository;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.domain.Member;
import ok.cherry.member.exception.MemberError;
import ok.cherry.member.infrastructure.MemberRepository;
import ok.cherry.product.domain.Product;
import ok.cherry.product.exception.ProductError;
import ok.cherry.product.infrastructure.ProductRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;
	private final MemberRepository memberRepository;
	private final ProductRepository productRepository;

	public CartAddResponse createCart(CartAddRequest request, String providerId) {
		Member member = memberRepository.findByProviderId(providerId)
			.orElseThrow(() -> new BusinessException(MemberError.USER_NOT_FOUND));

		Product product = productRepository.findById(request.productId())
			.orElseThrow(() -> new BusinessException(ProductError.PRODUCT_NOT_FOUND));

		Cart cart = Cart.create(
			member,
			product,
			product.getDailyRentalPrice(),
			request.color()
		);

		Cart savedCart = cartRepository.save(cart);

		return CartAddResponse.of(savedCart.getId());
	}

	public void deleteCart(CartDeleteRequest request, String providerId) {
		request.cartIds().stream().map(cartId -> cartRepository.findById(cartId)
			.orElseThrow(() -> new BusinessException(CartError.CART_NOT_FOUND))).forEach(cart -> {
			validateCartOwner(cart, providerId);
			deleteCart(cart);
		});
	}

	private static void validateCartOwner(Cart cart, String providerId) {
		if (!cart.getMember().getProviderId().equals(providerId)) {
			throw new BusinessException(CartError.UNAUTHORIZED_CART_ACCESS);
		}
	}

	private void deleteCart(Cart cart){
		try {
			cartRepository.delete(cart);
		} catch (Exception e) {
			throw new BusinessException(CartError.CART_DELETE_FAIL);
		}
	}


}
