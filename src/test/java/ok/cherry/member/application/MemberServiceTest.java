package ok.cherry.member.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.member.infrastructure.MemberRepository;

@SpringBootTest
@Transactional
class MemberServiceTest {

	@Autowired
	MemberService memberService;

	@Autowired
	MemberRepository memberRepository;

	@Test
	@DisplayName("중복된 이메일이면 true를 반환한다")
	void checkEmailDuplication_true() {
		// given
		Member member = MemberBuilder.create();
		memberRepository.save(member);

		// when
		boolean result = memberService.verifyEmailDuplication("test@test.com");

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("중복되지 않은 이메일이면 false를 반환한다")
	void checkEmailDuplication_false() {
		// given
		Member member = MemberBuilder.create();
		memberRepository.save(member);

		// when
		boolean result = memberService.verifyEmailDuplication("new@test.com");

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("중복된 닉네임이면 true를 반환한다")
	void checkNicknameDuplication_true() {
		// given
		Member member = MemberBuilder.create();
		memberRepository.save(member);

		// when
		boolean result = memberService.verifyNicknameDuplication("tester");

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("중복되지 않은 닉네임이면 false를 반환한다")
	void checkNicknameDuplication_false() {
		// given
		Member member = MemberBuilder.create();
		memberRepository.save(member);

		// when
		boolean result = memberService.verifyNicknameDuplication("new");

		// then
		assertThat(result).isFalse();
	}
}