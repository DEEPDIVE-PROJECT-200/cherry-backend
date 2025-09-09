package ok.cherry.rental.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ok.cherry.global.swagger.rental.RentalControllerDoc;
import ok.cherry.rental.application.RentalApplicationService;
import ok.cherry.rental.application.request.PlaceRentalOrderRequest;
import ok.cherry.rental.application.response.PlaceRentalOrderResponse;

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

		PlaceRentalOrderResponse response = rentalApplicationService.placeRentalOrder(providerId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
