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
import ok.cherry.auth.application.dto.request.LoginRequest;
import ok.cherry.auth.application.dto.request.SignUpRequest;
import ok.cherry.auth.application.dto.response.ReissueTokenResponse;
import ok.cherry.auth.application.dto.response.SignUpResponse;
import ok.cherry.auth.application.dto.response.TokenResponse;
import ok.cherry.auth.exception.TokenError;
import ok.cherry.auth.jwt.TokenExtractor;
import ok.cherry.auth.util.CookieManager;
import ok.cherry.global.exception.error.BusinessException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

	private final AuthService authService;
	private final TokenExtractor tokenExtractor;
	private final CookieManager cookieManager;

	@PostMapping("/signup")
	public ResponseEntity<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest request) {
		SignUpResponse response = authService.signUp(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(
		HttpServletResponse response,
		@RequestBody @Valid LoginRequest request
	) {
		TokenResponse tokenResponse = authService.login(request.providerId());
		cookieManager.setCookie(response, REFRESH_TOKEN_COOKIE_NAME, tokenResponse.refreshToken());
		return ResponseEntity.ok(tokenResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(
		HttpServletRequest request,
		HttpServletResponse response,
		@AuthenticationPrincipal(expression = "username") String providerId
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
