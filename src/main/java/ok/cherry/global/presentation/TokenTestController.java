package ok.cherry.auth.presentation;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.auth.application.dto.response.TokenResponse;
import ok.cherry.auth.jwt.TokenGenerator;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.global.swagger.auth.TokenTestControllerDoc;
import ok.cherry.member.domain.Member;
import ok.cherry.member.exception.MemberError;
import ok.cherry.member.infrastructure.MemberRepository;

@RestController
@Slf4j
@RequestMapping("/test")
@RequiredArgsConstructor
@Profile("local")
public class TokenTestController implements TokenTestControllerDoc {

	private final TokenGenerator tokenGenerator;
	private final MemberRepository memberRepository;

	@PostMapping("/token/{providerId}")
	public ResponseEntity<TokenResponse> generateAccessTokenForTest(@PathVariable String providerId) {
		Member member = memberRepository.findByProviderId(providerId)
			.orElseThrow(() -> new BusinessException(MemberError.USER_NOT_FOUND));

		TokenResponse tokenResponse = tokenGenerator.generateTokenDTO(member);
		log.info("token generated: {}", tokenResponse);
		return ResponseEntity.ok(tokenResponse);
	}
}