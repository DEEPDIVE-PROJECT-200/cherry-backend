package ok.cherry.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ok.cherry.member.infrastructure.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;

	public boolean verifyEmailDuplication(String emailAddress) {
		return memberRepository.existsByEmailAddress(emailAddress);
	}

	public boolean verifyNicknameDuplication(String nickname) {
		return memberRepository.existsByNickname(nickname);
	}
}
