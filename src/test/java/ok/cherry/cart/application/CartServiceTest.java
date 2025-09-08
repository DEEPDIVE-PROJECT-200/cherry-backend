package ok.cherry.cart.application;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

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
import ok.cherry.cart.application.dto.response.CartGetResponse;
import ok.cherry.cart.application.dto.response.CartInfoResponse;
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
	@DisplayName("장바구니에 상품을 3개를 초과하여 담으려고 하면 예외가 발생한다")
	void createCart_fail_cart_limit_exceeded() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(
			ProductBuilder.builder()
				.withColors(List.of(Color.BLACK, Color.MIDNIGHT_BLUE, Color.WHITE, Color.BLUE))
				.build()
		);

		Cart cart1 = CartBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct)
			.withColor(Color.BLACK)
			.build();

		Cart cart2 = CartBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct)
			.withColor(Color.MIDNIGHT_BLUE)
			.build();

		Cart cart3 = CartBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct)
			.withColor(Color.WHITE)
			.build();

		cartRepository.saveAll(List.of(cart1, cart2, cart3));

		CartCreateRequest request = new CartCreateRequest(
			savedProduct.getId(),
			Color.BLUE
		);

		// when & then
		assertThatThrownBy(() -> cartService.createCart(request, savedMember.getProviderId()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(CartError.CART_LIMIT_EXCEEDED.getMessage());
	}

	@Test
	@DisplayName("장바구니에 같은 상품의 같은 색상을 담으려고 하면 예외가 발생한다")
	void createCart_fail_cart_duplicate() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());
		Cart savedCart = cartRepository.save(
			CartBuilder.builder()
				.withMember(savedMember)
				.withProduct(savedProduct)
				.build()
		);

		CartCreateRequest request = new CartCreateRequest(
			savedProduct.getId(),
			savedCart.getColor()
		);

		// when & then
		assertThatThrownBy(() -> cartService.createCart(request, savedMember.getProviderId()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(CartError.DUPLICATE_CART.getMessage());
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
		flushAndClear();

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

	@Test
	@DisplayName("장바구니에 담긴 상품이 존재할 경우 담긴 상품 정보를 반환하며 조회에 성공한다")
	void getCarts_success_cart_exist() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(
			ProductBuilder.builder()
				.withColors(List.of(Color.BLACK, Color.MIDNIGHT_BLUE, Color.WHITE, Color.BLUE))
				.build()
		);

		Cart cart1 = CartBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct)
			.withColor(Color.BLACK)
			.build();

		Cart cart2 = CartBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct)
			.withColor(Color.MIDNIGHT_BLUE)
			.build();

		Cart cart3 = CartBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct)
			.withColor(Color.WHITE)
			.build();

		cartRepository.saveAll(List.of(cart1, cart2, cart3));

		// when
		CartGetResponse response = cartService.getCarts(savedMember.getProviderId());

		// then
		assertThat(response.carts()).hasSize(3);
		assertThat(response.carts()).extracting("color")
			.containsExactlyInAnyOrder(Color.BLACK, Color.MIDNIGHT_BLUE, Color.WHITE);

		BigDecimal expectedTotalPrice = Stream.of(cart1, cart2, cart3)
			.map(Cart::getPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		assertThat(response.totalPrice()).isEqualTo(expectedTotalPrice);
	}

	@Test
	@DisplayName("장바구니에 담긴 상품이 존재하지 않을 경우 빈 리스트를 반환하며 조회에 성공한다")
	void getCarts_success_cart_nonExist() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());

		// when
		CartGetResponse response = cartService.getCarts(savedMember.getProviderId());

		// then
		assertThat(response.carts()).isEmpty();
		assertThat(response.totalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Test
	@DisplayName("장바구니를 조회하려는 사용자를 찾을 수 없어 예외가 발생한다")
	void getCarts_fail_member_not_found() {
		// given
		String nonExistProviderId = "12345";

		// when & then
		assertThatThrownBy(() -> cartService.getCarts(nonExistProviderId))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberError.USER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("cartIds로 장바구니 조회에 성공한다")
	void getCartsByIds_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product product1 = productRepository.save(ProductBuilder.create());
		Product product2 = productRepository.save(ProductBuilder.builder().withName("Product2").build());

		Cart cart1 = cartRepository.save(CartBuilder.builder()
			.withMember(savedMember)
			.withProduct(product1)
			.withColor(Color.BLACK)
			.build());

		Cart cart2 = cartRepository.save(CartBuilder.builder()
			.withMember(savedMember)
			.withProduct(product2)
			.withColor(Color.WHITE)
			.build());

		List<Long> cartIds = List.of(cart1.getId(), cart2.getId());
		flushAndClear();

		// when
		List<CartInfoResponse> responses = cartService.getCartsByIds(cartIds, savedMember.getProviderId());

		// then
		assertThat(responses).hasSize(2);

		CartInfoResponse response1 = responses.stream()
			.filter(r -> r.cartId().equals(cart1.getId()))
			.findFirst()
			.orElseThrow();

		CartInfoResponse response2 = responses.stream()
			.filter(r -> r.cartId().equals(cart2.getId()))
			.findFirst()
			.orElseThrow();

		assertThat(response1.productId()).isEqualTo(product1.getId());
		assertThat(response1.productName()).isEqualTo(product1.getName());
		assertThat(response1.color()).isEqualTo(Color.BLACK);
		assertThat(response1.dailyRentalPrice()).isEqualByComparingTo(cart1.getPrice());

		assertThat(response2.productId()).isEqualTo(product2.getId());
		assertThat(response2.productName()).isEqualTo(product2.getName());
		assertThat(response2.color()).isEqualTo(Color.WHITE);
		assertThat(response2.dailyRentalPrice()).isEqualByComparingTo(cart2.getPrice());
	}

	@Test
	@DisplayName("존재하지 않는 cartId가 포함된 경우 예외가 발생한다")
	void getCartsByIds_fail_cartNotFound() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());

		Cart savedCart = cartRepository.save(CartBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct)
			.build());

		Long nonExistentCartId = 999L;
		List<Long> cartIds = List.of(savedCart.getId(), nonExistentCartId);

		// when & then
		assertThatThrownBy(() -> cartService.getCartsByIds(cartIds, savedMember.getProviderId()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(CartError.CART_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("다른 사용자의 장바구니 조회 시 권한 없음 예외가 발생한다")
	void getCartsByIds_fail_unauthorizedAccess() {
		// given
		Member cartOwner = memberRepository.save(MemberBuilder.create());
		Member anotherMember = memberRepository.save(
			MemberBuilder.builder()
				.withProviderId("another_provider_id")
				.withEmail("another@test.com")
				.withNickname("another")
				.build()
		);
		Product savedProduct = productRepository.save(ProductBuilder.create());

		Cart cart = cartRepository.save(CartBuilder.builder()
			.withMember(cartOwner)
			.withProduct(savedProduct)
			.build());

		List<Long> cartIds = List.of(cart.getId());

		// when & then
		assertThatThrownBy(() -> cartService.getCartsByIds(cartIds, anotherMember.getProviderId()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(CartError.UNAUTHORIZED_CART_ACCESS.getMessage());
	}

	@Test
	@DisplayName("여러 사용자의 장바구니가 섞인 경우 권한 없음 예외가 발생한다")
	void getCartsByIds_fail_mixedOwnership() {
		// given
		Member member1 = memberRepository.save(MemberBuilder.create());
		Member member2 = memberRepository.save(
			MemberBuilder.builder()
				.withProviderId("member2_provider_id")
				.withEmail("member2@test.com")
				.withNickname("member2")
				.build()
		);
		Product savedProduct = productRepository.save(ProductBuilder.create());

		Cart cart1 = cartRepository.save(CartBuilder.builder()
			.withMember(member1)
			.withProduct(savedProduct)
			.build());

		Cart cart2 = cartRepository.save(CartBuilder.builder()
			.withMember(member2)
			.withProduct(savedProduct)
			.build());

		List<Long> cartIds = List.of(cart1.getId(), cart2.getId());

		// when & then
		assertThatThrownBy(() -> cartService.getCartsByIds(cartIds, member1.getProviderId()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(CartError.UNAUTHORIZED_CART_ACCESS.getMessage());
	}

	@Test
	@DisplayName("빈 cartIds 리스트로 조회 시 빈 리스트가 반환된다")
	void getCartsByIds_withEmptyList_returnsEmptyList() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		List<Long> emptyCartIds = List.of();

		// when
		List<CartInfoResponse> responses = cartService.getCartsByIds(emptyCartIds, savedMember.getProviderId());

		// then
		assertThat(responses).isEmpty();
	}

	@Test
	@DisplayName("단일 cartId로 장바구니 조회에 성공한다")
	void getCartsByIds_singleCart_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());
		Product savedProduct = productRepository.save(ProductBuilder.create());

		Cart savedCart = cartRepository.save(CartBuilder.builder()
			.withMember(savedMember)
			.withProduct(savedProduct)
			.withColor(Color.MIDNIGHT_BLUE)
			.build());

		List<Long> cartIds = List.of(savedCart.getId());

		// when
		List<CartInfoResponse> responses = cartService.getCartsByIds(cartIds, savedMember.getProviderId());

		// then
		assertThat(responses).hasSize(1);

		CartInfoResponse response = responses.getFirst();
		assertThat(response.cartId()).isEqualTo(savedCart.getId());
		assertThat(response.productId()).isEqualTo(savedProduct.getId());
		assertThat(response.productName()).isEqualTo(savedProduct.getName());
		assertThat(response.productThumbnailUrl()).isEqualTo(savedProduct.getThumbnailUrl());
		assertThat(response.color()).isEqualTo(Color.MIDNIGHT_BLUE);
		assertThat(response.dailyRentalPrice()).isEqualByComparingTo(savedCart.getPrice());
	}

	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}