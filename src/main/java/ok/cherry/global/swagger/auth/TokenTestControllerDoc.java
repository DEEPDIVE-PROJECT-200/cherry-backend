package ok.cherry.global.swagger.auth;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ok.cherry.auth.application.dto.response.TokenResponse;

@Tag(name = "Dev", description = "🔧 개발 도구 - 개발용 테스트 API (개발 환경 전용)")
public interface TokenTestControllerDoc {

	@Operation(method = "POST", summary = "providerId로 토큰 발급", description = "providerId로 토큰을 발급합니다. 🚨local 환경에서만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "토큰 발급 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
		@ApiResponse(responseCode = "404", description = "토큰 발급 실패 - 존재하지 않는 providerId",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
	})
	ResponseEntity<TokenResponse> generateAccessTokenForTest(
		@Parameter(description = "회원 등록시 저장된 providerId") String providerId
	);
}
