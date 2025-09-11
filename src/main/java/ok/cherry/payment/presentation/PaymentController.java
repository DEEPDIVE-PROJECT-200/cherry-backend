package ok.cherry.payment.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ok.cherry.global.swagger.payment.PaymentControllerDoc;
import ok.cherry.payment.application.PaymentService;
import ok.cherry.payment.application.dto.response.PaymentResponse;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController implements PaymentControllerDoc {

	private final PaymentService paymentService;

	@GetMapping("/{paymentId}")
	public ResponseEntity<PaymentResponse> getPayment(
		@PathVariable Long paymentId,
		@AuthenticationPrincipal String providerId
	) {
		PaymentResponse response = paymentService.getPayment(paymentId, providerId);
		return ResponseEntity.ok(response);
	}
}
