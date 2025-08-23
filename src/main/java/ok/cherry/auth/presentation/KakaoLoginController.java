package ok.cherry.auth.presentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.auth.application.KakaoAuthService;
import ok.cherry.auth.application.dto.response.CheckMemberResponse;

@Slf4j
@Controller
@RequestMapping()
@RequiredArgsConstructor
public class KakaoLoginController {

	private final KakaoAuthService kakaoAuthService;

	@Value("${kakao.client_id}")
	private String client_id;

	@Value("${kakao.redirect_uri}")
	private String redirect_uri;

	// 카카오 로그인 테스트용 페이지 컨트롤러
	@GetMapping("/login/page")
	public String loginPage(Model model) {
		String location =
			"https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + client_id + "&redirect_uri="
				+ redirect_uri;
		model.addAttribute("location", location);

		return "login";
	}

	@GetMapping("/callback")
	public ResponseEntity<CheckMemberResponse> callback(@RequestParam("code") String code) {
		log.info(code);
		String accessToken = kakaoAuthService.getAccessToken(code);
		String providerId = kakaoAuthService.getProviderId(accessToken);
		CheckMemberResponse response = kakaoAuthService.checkMember(providerId);

		log.info("CheckMemberResponse: {}", response);
		return ResponseEntity.ok(response);
	}
}
