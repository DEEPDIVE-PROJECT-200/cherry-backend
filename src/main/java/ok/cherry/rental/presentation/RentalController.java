package ok.cherry.rental.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ok.cherry.global.swagger.rental.RentalControllerDoc;
import ok.cherry.payment.application.dto.response.PaymentResponse;
import ok.cherry.rental.application.RentalApplicationService;
import ok.cherry.rental.application.RentalService;
import ok.cherry.rental.application.request.PlaceRentalOrderRequest;
import ok.cherry.rental.application.response.PlaceRentalOrderResponse;
import ok.cherry.rental.application.response.RentalGetResponse;
import ok.cherry.rental.application.response.RentalInfoResponse;
import ok.cherry.rental.domain.Rental;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
public class RentalController implements RentalControllerDoc {

	private final RentalService rentalService;
	private final RentalApplicationService rentalApplicationService;

	@GetMapping("{rentalId}")
	public ResponseEntity<RentalInfoResponse> getRental(
		@PathVariable Long rentalId,
		@AuthenticationPrincipal String providerId
	) {
		Rental rental = rentalService.getRental(rentalId, providerId);
		RentalInfoResponse response = RentalInfoResponse.from(rental);
		return ResponseEntity.ok(response);
	}

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

	@GetMapping("/{rentalId}/payment")
	public ResponseEntity<PaymentResponse> getPaymentByRentalId(
		@PathVariable Long rentalId,
		@AuthenticationPrincipal String providerId
	) {
		PaymentResponse response = rentalApplicationService.getPaymentByRentalId(rentalId, providerId);
		return ResponseEntity.ok(response);
	}
}