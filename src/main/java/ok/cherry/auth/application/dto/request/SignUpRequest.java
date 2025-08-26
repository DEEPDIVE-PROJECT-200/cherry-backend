package ok.cherry.auth.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(

	@NotBlank(message = "임시 토큰은 필수입니다")
	String tempToken,

	@Email(message = "올바른 이메일 형식이 아닙니다")
	@NotBlank(message = "이메일은 필수입니다")
	String emailAddress,

	@NotBlank(message = "닉네임은 필수입니다")
	@Size(min = 2, max = 10)
	String nickname
) {
}
