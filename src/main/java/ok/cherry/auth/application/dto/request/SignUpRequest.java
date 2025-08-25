package ok.cherry.auth.application.dto.request;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(

	@NotBlank(message = "providerId는 필수입니다")
	String providerId,

	@Email(message = "올바른 이메일 형식이 아닙니다")
	@NotBlank(message = "이메일은 필수입니다")
	String emailAdress,

	@NotBlank(message = "닉네임은 필수입니다")
	@Range(min = 2, max = 10)
	String nickname
) {
}
