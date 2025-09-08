package ok.cherry.member.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ok.cherry.global.swagger.Member.MemberControllerDoc;
import ok.cherry.member.application.MemberService;
import ok.cherry.member.application.dto.request.EmailDuplicationRequest;
import ok.cherry.member.application.dto.request.NicknameDuplicationRequest;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDoc {

	private final MemberService memberService;

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
}
