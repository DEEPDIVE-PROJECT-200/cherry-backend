package ok.cherry.payment.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ok.cherry.payment.application.PaymentService;
import ok.cherry.payment.application.dto.response.PaymentResponse;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@GetMapping("/{paymentId}")
	public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long paymentId) {
		PaymentResponse response = paymentService.getPayment(paymentId);
		return ResponseEntity.ok(response);
	}
}
