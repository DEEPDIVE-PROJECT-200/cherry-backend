package ok.cherry.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ok.cherry.auth.application.AuthService;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.domain.Member;
import ok.cherry.member.exception.MemberError;
import ok.cherry.member.infrastructure.MemberRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberApplicationService {

	private final MemberRepository memberRepository;
	private final AuthService authService;

	public void deactivateMember(String accessToken, String providerId) {
		Member member = memberRepository.findByProviderId(providerId)
			.orElseThrow(() -> new BusinessException(MemberError.USER_NOT_FOUND));

		member.deactivate();
		authService.logout(accessToken, providerId);
	}
}
