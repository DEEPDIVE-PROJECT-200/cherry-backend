package ok.cherry.shipping.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ok.cherry.shipping.application.ShippingService;
import ok.cherry.shipping.application.response.TrackingNumberResponse;

@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {

	private final ShippingService shippingService;

	@GetMapping("/{rentalId}")
	public ResponseEntity<TrackingNumberResponse> getTrackingNumber(@PathVariable Long rentalId) {
		TrackingNumberResponse response = shippingService.getTrackingNumber(rentalId);
		return ResponseEntity.ok(response);
	}
}
