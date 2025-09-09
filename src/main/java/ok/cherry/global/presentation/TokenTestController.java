package ok.cherry.global.presentation;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.auth.application.AuthService;
import ok.cherry.auth.application.dto.response.TokenResponse;
import ok.cherry.auth.util.CookieManager;

@RestController
@Slf4j
@RequestMapping("/test")
@RequiredArgsConstructor
@Profile("local")
public class TokenTestController {

	private final AuthService authService;
	private final CookieManager cookieManager;

	@PostMapping("/token/{providerId}")
	public ResponseEntity<TokenResponse> generateAccessTokenForTest(
		HttpServletResponse response,
		@PathVariable String providerId
	) {
		TokenResponse tokenResponse = authService.login(providerId);
		cookieManager.setCookie(response, "refreshToken", tokenResponse.refreshToken());
		log.info("token generated: {}", tokenResponse);
		return ResponseEntity.ok(tokenResponse);
	}
}