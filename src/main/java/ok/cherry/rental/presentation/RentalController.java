package ok.cherry.rental.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ok.cherry.global.swagger.rental.RentalControllerDoc;
import ok.cherry.rental.application.RentalService;
import ok.cherry.rental.application.response.RentalGetResponse;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
public class RentalController implements RentalControllerDoc {

	private final RentalService rentalService;

	@GetMapping
	public ResponseEntity<RentalGetResponse> getRentals(
		@RequestParam(required = false) Long lastRentalId,
		@RequestParam(defaultValue = "2") int limit,
		@AuthenticationPrincipal String providerId
	) {
		RentalGetResponse response = rentalService.getRentals(lastRentalId, limit, providerId);
		return ResponseEntity.ok(response);
	}

    @PostMapping
	public ResponseEntity<PlaceRentalOrderResponse> placeRentalOrder(@RequestBody PlaceRentalOrderRequest request,
		@AuthenticationPrincipal String providerId) {

		log.info("=== RentalController 디버깅 ===");
		log.info("받은 providerId: {}", providerId);
		log.info("Authentication 정보: {}", SecurityContextHolder.getContext().getAuthentication());
		PlaceRentalOrderResponse response = rentalApplicationService.placeRentalOrder(providerId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
