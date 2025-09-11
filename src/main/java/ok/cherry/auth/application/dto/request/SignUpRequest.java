package ok.cherry.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청 DTO")
public record SignUpRequest(

	@Schema(description = "소셜 로그인 성공 후 발급된 임시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	@NotBlank(message = "임시 토큰은 필수입니다")
	String tempToken,

	@Schema(description = "사용자 이메일 주소", example = "user@example.com")
	@Email(message = "올바른 이메일 형식이 아닙니다")
	@NotBlank(message = "이메일은 필수입니다")
	String emailAddress,

	@Schema(description = "닉네임 (2~10자)", example = "nickname")
	@NotBlank(message = "닉네임은 필수입니다")
	@Size(min = 2, max = 10)
	String nickname
) {
}
