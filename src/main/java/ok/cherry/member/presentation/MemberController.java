package ok.cherry.member.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ok.cherry.auth.jwt.TokenExtractor;
import ok.cherry.global.swagger.member.MemberControllerDoc;
import ok.cherry.member.application.MemberApplicationService;
import ok.cherry.member.application.MemberService;
import ok.cherry.member.application.dto.request.EmailDuplicationRequest;
import ok.cherry.member.application.dto.request.NicknameDuplicationRequest;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDoc {

	private final MemberService memberService;
	private final MemberApplicationService memberApplicationService;
	private final TokenExtractor tokenExtractor;

	@PostMapping("/verify/email")
	public ResponseEntity<Void> verifyEmailDuplication(@Valid @RequestBody EmailDuplicationRequest request) {
		if (memberService.verifyEmailDuplication(request.emailAddress())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		return ResponseEntity.ok().build();
	}

	@PostMapping("/verify/nickname")
	public ResponseEntity<Void> verifyNicknameDuplication(@Valid @RequestBody NicknameDuplicationRequest request) {
		if (memberService.verifyNicknameDuplication(request.nickname())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> deactivateMember(
		HttpServletRequest request,
		@AuthenticationPrincipal String providerId
	) {
		String accessToken = tokenExtractor.resolveToken(request);
		memberApplicationService.deactivateMember(accessToken, providerId);
		return ResponseEntity.noContent().build();
	}

}
