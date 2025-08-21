package ok.cherry.auth.presentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping()
public class KakaoLoginPageController {

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
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
