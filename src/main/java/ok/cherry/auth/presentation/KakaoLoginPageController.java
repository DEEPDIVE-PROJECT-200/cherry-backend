package ok.cherry.auth.presentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.auth.application.KakaoAuthService;

@Slf4j
@Controller
@RequestMapping()
@RequiredArgsConstructor
public class KakaoLoginPageController {

	private final KakaoAuthService kakaoAuthService;

	@Value("${kakao.client_id}")
	private String client_id;

	@Value("${kakao.redirect_uri}")
	private String redirect_uri;

	@GetMapping("/login/page")
	public String loginPage(Model model) {
		String location =
			"https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + client_id + "&redirect_uri="
				+ redirect_uri;
		model.addAttribute("location", location);

		return "login";
	}

	@GetMapping("/callback")
	public ResponseEntity<?> callback(@RequestParam("code") String code) {
		log.info(code);
		String accessToken = kakaoAuthService.getAccessToken(code);
		String providerId = kakaoAuthService.getProviderId(accessToken); // providerId 까지는 무조건 가져올 수 있음

		// todo: providerId 값으로 db에 사용자 정보가 있는지 조회

		log.info("callback end");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// todo: 로그인 controller
}
