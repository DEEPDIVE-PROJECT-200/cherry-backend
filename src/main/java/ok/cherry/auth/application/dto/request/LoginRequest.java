package ok.cherry.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

	@NotBlank(message = "providerId는 필수입니다")
	String providerId
) {
}
