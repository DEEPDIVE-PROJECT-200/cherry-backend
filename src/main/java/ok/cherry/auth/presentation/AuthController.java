package ok.cherry.auth.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ok.cherry.auth.application.AuthService;
import ok.cherry.auth.application.dto.request.SignUpRequest;
import ok.cherry.auth.application.dto.response.ReissueTokenResponse;
import ok.cherry.auth.application.dto.response.TokenResponse;
import ok.cherry.auth.exception.AuthError;
import ok.cherry.auth.exception.TokenError;
import ok.cherry.auth.jwt.TokenExtractor;
import ok.cherry.auth.util.CookieManager;
import ok.cherry.global.exception.error.BusinessException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

	private final AuthService authService;
	private final TokenExtractor tokenExtractor;
	private final CookieManager cookieManager;

	@PostMapping("/signup")
	public ResponseEntity<TokenResponse> signUp(
		HttpServletResponse response,
		@RequestBody @Valid SignUpRequest request
	) {
		// 1. 임시 토큰 검증 및 providerId 추출
		String providerId = tokenExtractor.getProviderId(request.tempToken());
		if (providerId == null) {
			throw new BusinessException(AuthError.INVALID_TEMP_TOKEN);
		}

		// 2. 회원가입 처리 (검증된 providerId 사용)
		authService.signUp(providerId, request.emailAddress(), request.nickname());
		
		// 3. 회원가입 완료 후 즉시 JWT 토큰 발급
		TokenResponse tokenResponse = authService.login(providerId);
		cookieManager.setCookie(response, REFRESH_TOKEN_COOKIE_NAME, tokenResponse.refreshToken());
		
		return ResponseEntity.ok(tokenResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(
		HttpServletRequest request,
		HttpServletResponse response,
		@AuthenticationPrincipal String providerId
	) {
		String accessToken = tokenExtractor.resolveToken(request);
		authService.logout(accessToken, providerId);
		cookieManager.removeCookie(response, REFRESH_TOKEN_COOKIE_NAME);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/reissue")
	public ResponseEntity<ReissueTokenResponse> reissue(
		@CookieValue(value = "refreshToken", required = false) String refreshToken
	) {
		if (refreshToken == null || refreshToken.isEmpty()) {
			throw new BusinessException(TokenError.INVALID_REFRESH_TOKEN);
		}

		ReissueTokenResponse tokenResponse = authService.reissueAccessToken(refreshToken);
		return ResponseEntity.ok(tokenResponse);
	}
}
