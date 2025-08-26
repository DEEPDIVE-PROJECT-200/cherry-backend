package ok.cherry.auth.presentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.auth.application.AuthService;
import ok.cherry.auth.application.KakaoOAuthService;
import ok.cherry.auth.application.dto.response.CheckMemberResponse;
import ok.cherry.auth.application.dto.response.SignUpRequiredResponse;
import ok.cherry.auth.application.dto.response.TokenResponse;
import ok.cherry.auth.exception.AuthError;
import ok.cherry.auth.util.CookieManager;
import ok.cherry.auth.util.StateGenerator;
import ok.cherry.auth.util.TempTokenGenerator;
import ok.cherry.global.exception.error.BusinessException;

@Slf4j
@Controller
@RequestMapping()
@RequiredArgsConstructor
public class KakaoLoginController {

	private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
	
	private final KakaoOAuthService kakaoOAuthService;
	private final AuthService authService;
	private final StateGenerator stateGenerator;
	private final TempTokenGenerator tempTokenGenerator;
	private final CookieManager cookieManager;

	@Value("${kakao.client_id}")
	private String client_id;

	@Value("${kakao.redirect_uri}")
	private String redirect_uri;

	// 카카오 로그인 테스트용 페이지 컨트롤러
	@GetMapping("/login/page")
	public String loginPage(Model model) {
		String state = stateGenerator.generateState();
		String location =
			"https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + client_id 
			+ "&redirect_uri=" + redirect_uri + "&state=" + state;
		model.addAttribute("location", location);

		return "login";
	}

	// 로그인/회원가입 분기 처리
	@GetMapping("/auth/callback")
	public ResponseEntity<?> callback(
		HttpServletResponse response,
		@RequestParam("code") String code,
		@RequestParam("state") String state
	) {
		// 1. State 검증 (CSRF 보호)
		if (!stateGenerator.validateAndRemoveState(state)) {
			throw new BusinessException(AuthError.INVALID_OAUTH_STATE);
		}

		// 2. OAuth 인증 처리
		String accessToken = kakaoOAuthService.getAccessToken(code);
		String providerId = kakaoOAuthService.getProviderId(accessToken);
		
		// 3. 회원가입 여부 확인
		CheckMemberResponse checkResponse = kakaoOAuthService.checkMember(providerId);
		
		if (checkResponse.isMember()) {
			// 4-1. 기존 회원 -> JWT 토큰 발급
			TokenResponse tokenResponse = authService.login(providerId);
			cookieManager.setCookie(response, REFRESH_TOKEN_COOKIE_NAME, tokenResponse.refreshToken());

			return ResponseEntity.ok(tokenResponse);
		} else {
			// 4-2. 신규 사용자 -> 임시 토큰 발급 (회원가입 필요)
			String tempToken = tempTokenGenerator.generateTempToken(providerId);
			
			return ResponseEntity.status(202)
				.body(new SignUpRequiredResponse(tempToken));
		}
	}
}
