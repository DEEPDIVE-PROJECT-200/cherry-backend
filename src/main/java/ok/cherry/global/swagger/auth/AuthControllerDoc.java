package ok.cherry.global.swagger.auth;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ok.cherry.auth.application.dto.request.SignUpRequest;
import ok.cherry.auth.application.dto.response.AccessTokenResponse;

@Tag(name = "Authentication", description = "🔐 인증 API - 회원가입, 로그인, 로그아웃, 토큰 재발급 API")
public interface AuthControllerDoc {

	@Operation(method = "POST", summary = "회원가입", description = "소셜 로그인 성공 후 발급된 임시 토큰을 사용하여 회원가입을 진행합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원가입 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessTokenResponse.class))),
		@ApiResponse(responseCode = "400", description = "회원가입 실패 - 유효성 검사 실패",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
		@ApiResponse(responseCode = "404", description = "회원가입 실패 - 존재하지 않는 임시 토큰",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
	})
	ResponseEntity<AccessTokenResponse> signUp(HttpServletResponse response, SignUpRequest request);

	@Operation(method = "POST", summary = "로그아웃", description = "로그아웃을 진행합니다. 서버에 저장된 Refresh Token을 삭제하고, Access Token을 블랙리스트에 추가합니다.",
		security = {@SecurityRequirement(name = "JWT")}
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그아웃 성공"),
		@ApiResponse(responseCode = "401", description = "로그아웃 실패 - 유효하지 않은 토큰",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
	})
	ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response, String providerId);

	@Operation(method = "POST", summary = "Access Token 재발급", description = "Refresh Token을 사용하여 새로운 Access Token을 재발급합니다.",
		security = {@SecurityRequirement(name = "JWT")}
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Access Token 재발급 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessTokenResponse.class))),
		@ApiResponse(responseCode = "401", description = "Access Token 재발급 실패 - 유효하지 않은 Refresh Token",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
	})
	ResponseEntity<AccessTokenResponse> reissue(String refreshToken);
}
