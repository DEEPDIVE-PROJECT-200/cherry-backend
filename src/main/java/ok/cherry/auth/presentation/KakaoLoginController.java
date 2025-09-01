package ok.cherry.auth.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.auth.application.AuthService;
import ok.cherry.auth.application.KakaoOAuthService;
import ok.cherry.auth.application.dto.response.CheckMemberResponse;
import ok.cherry.auth.application.dto.response.SignUpRequiredResponse;
import ok.cherry.auth.application.dto.response.TokenResponse;
import ok.cherry.auth.util.CookieManager;
import ok.cherry.auth.util.TempTokenGenerator;

@Slf4j
@Controller
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
public class KakaoLoginController {

	private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

	private final KakaoOAuthService kakaoOAuthService;
	private final AuthService authService;
	private final TempTokenGenerator tempTokenGenerator;
	private final CookieManager cookieManager;

	// 로그인/회원가입 분기 처리
	@PostMapping("/callback")
	public ResponseEntity<?> callback(
		HttpServletResponse response,
		@RequestParam("code") String code
	) {
		// 1. OAuth 인증 처리
		String accessToken = kakaoOAuthService.getAccessToken(code);
		String providerId = kakaoOAuthService.getProviderId(accessToken);

		// 2. 회원가입 여부 확인
		CheckMemberResponse checkResponse = kakaoOAuthService.checkMember(providerId);

		if (checkResponse.isMember()) {
			// 3-1. 기존 회원 -> JWT 토큰 발급
			TokenResponse tokenResponse = authService.login(providerId);
			cookieManager.setCookie(response, REFRESH_TOKEN_COOKIE_NAME, tokenResponse.refreshToken());

			return ResponseEntity.ok(tokenResponse);
		} else {
			// 3-2. 신규 사용자 -> 임시 토큰 발급 (회원가입 필요)
			String tempToken = tempTokenGenerator.generateTempToken(providerId);

			return ResponseEntity.status(202)
				.body(new SignUpRequiredResponse(tempToken));
		}
	}
}
