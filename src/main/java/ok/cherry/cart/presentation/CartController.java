package ok.cherry.cart.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ok.cherry.cart.application.CartService;
import ok.cherry.cart.application.dto.request.CartAddRequest;
import ok.cherry.cart.application.dto.request.CartDeleteRequest;
import ok.cherry.cart.application.dto.response.CartAddResponse;
import ok.cherry.global.swagger.cart.CartControllerDoc;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController implements CartControllerDoc {

	private final CartService cartService;

	@PostMapping
	public ResponseEntity<CartAddResponse> createCart(@RequestBody CartAddRequest request,
		@AuthenticationPrincipal String providerId) {
		CartAddResponse response = cartService.createCart(request, providerId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteCart(@RequestBody CartDeleteRequest request,
		@AuthenticationPrincipal String providerId) {
		cartService.deleteCart(request, providerId);
		return ResponseEntity.ok().build();
	}

}
