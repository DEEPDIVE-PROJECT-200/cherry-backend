package ok.cherry.auth.security;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.domain.Member;
import ok.cherry.member.exception.MemberError;
import ok.cherry.member.infrastructure.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String providerId) throws UsernameNotFoundException {
		Member member = memberRepository.findByProviderId(providerId)
			.orElseThrow(() -> new BusinessException(MemberError.USER_NOT_FOUND));

		return User.builder()
			.username(member.getProviderId())
			.password("")
			.authorities(Collections.emptyList())
			.build();
	}
}
