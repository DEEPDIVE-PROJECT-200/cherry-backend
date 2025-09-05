package ok.cherry.cart.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import ok.cherry.cart.CartBuilder;
import ok.cherry.cart.application.dto.request.CartCreateRequest;
import ok.cherry.cart.application.dto.request.CartDeleteRequest;
import ok.cherry.cart.application.dto.response.CartCreateResponse;
import ok.cherry.cart.domain.Cart;
import ok.cherry.cart.exception.CartError;
import ok.cherry.cart.infrastructure.CartRepository;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.member.exception.MemberError;
import ok.cherry.member.infrastructure.MemberRepository;
import ok.cherry.product.ProductBuilder;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.type.Color;
import ok.cherry.product.exception.ProductError;
import ok.cherry.product.infrastructure.ProductRepository;

@SpringBootTest
@Transactional
class CartServiceTest {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartService cartService;

	@Autowired
	EntityManager entityManager;

	@Test
	@DisplayName("장바구니 상품 등록에 성공한다")
	void createCart_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());

		CartCreateRequest request = new CartCreateRequest(
			savedProduct.getId(),
			Color.BLACK
		);

		// when
		CartCreateResponse response = cartService.createCart(request, savedMember.getProviderId());
		flushAndClear();

		// then
		Cart savedCart = cartRepository.findById(response.cartId()).orElseThrow();
		assertThat(savedCart.getMember().getId()).isEqualTo(savedMember.getId());
		assertThat(savedCart.getProduct().getId()).isEqualTo(savedProduct.getId());
		assertThat(savedCart.getPrice()).isEqualByComparingTo(savedProduct.getDailyRentalPrice());
		assertThat(savedCart.getColor()).isEqualTo(Color.BLACK);
	}

	@Test
	@DisplayName("장바구니 상품을 추가하려는 회원을 찾을 수 없어 예외가 발생한다")
	void createCart_fail_member_not_found() {
		// given
		String nonExistProviderId = "12345";
		Product savedProduct = productRepository.save(ProductBuilder.create());

		CartCreateRequest request = new CartCreateRequest(
			savedProduct.getId(),
			Color.BLACK
		);

		// when & then
		assertThatThrownBy(() -> cartService.createCart(request, nonExistProviderId))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberError.USER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("장바구니에 추가하려는 상품을 찾을 수 없어 예외가 발생한다")
	void createCart_fail_product_not_found() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Long nonExistProductId = 1000L;

		CartCreateRequest request = new CartCreateRequest(
			nonExistProductId,
			Color.BLACK
		);

		// when & then
		assertThatThrownBy(() -> cartService.createCart(request, savedMember.getProviderId()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ProductError.PRODUCT_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("장바구니 상품 삭제에 성공한다")
	void deleteCart_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		Cart savedCart = cartRepository.save(
			CartBuilder.builder()
				.withMember(savedMember)
				.withProduct(savedProduct)
				.build()
		);

		CartDeleteRequest deleteRequest = new CartDeleteRequest(List.of(savedCart.getId()));

		// when
		cartService.deleteCart(deleteRequest, savedMember.getProviderId());

		// then
		assertThat(cartRepository.findById(savedCart.getId())).isEmpty();
	}

	@Test
	@DisplayName("삭제하려는 장바구니 상품을 찾을 수 없어 예외가 발생한다")
	void deleteCart_fail_cart_not_found() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());

		Long nonExistCartId = 1000L;
		CartDeleteRequest deleteRequest = new CartDeleteRequest(List.of(nonExistCartId));

		// when & then
		assertThatThrownBy(() -> cartService.deleteCart(deleteRequest, savedMember.getProviderId()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(CartError.CART_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("삭제를 요청한 사용자와 장바구니 상품 소유자가 일치하지 않아 예외가 발생한다")
	void deleteCart_fail_unauthorized_cart_access() {
		// given
		Member firstMember = memberRepository.save(MemberBuilder.create());
		Member secondMember = memberRepository.save(
			MemberBuilder.builder()
				.withProviderId("another_provider_id")
				.withEmail("another@test.com")
				.withNickname("another")
				.build()
		);
		Product savedProduct = productRepository.save(ProductBuilder.create());

		Cart savedCart = cartRepository.save(CartBuilder.builder()
			.withMember(firstMember)
			.withProduct(savedProduct)
			.build()
		);

		CartDeleteRequest request = new CartDeleteRequest(List.of(savedCart.getId()));

		// when & then
		assertThatThrownBy(() -> cartService.deleteCart(request, secondMember.getProviderId()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(CartError.UNAUTHORIZED_CART_ACCESS.getMessage());
	}

	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}