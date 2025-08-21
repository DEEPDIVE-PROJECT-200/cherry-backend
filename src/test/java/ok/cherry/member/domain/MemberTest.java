package ok.cherry.member.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ok.cherry.global.exception.error.DomainException;
import ok.cherry.member.exception.MemberError;

class MemberTest {

	@Test
	@DisplayName("회원 등록 시 상태가 활성화되고 등록일자가 설정된다")
	void registerMember() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when & then
		assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
		assertThat(member.getDetail().getRegisteredAt()).isNotNull();
	}

	@Test
	@DisplayName("잘못된 이메일 형식으로 회원 등록 시 예외가 발생한다")
	void invalidEmail() {
		// given
		String invalidEmail = "wrongEmail";

		// when & then
		assertThatThrownBy(() -> Member.register("12345", Provider.KAKAO, invalidEmail, "tester"))
			.isInstanceOf(DomainException.class)
			.hasMessage(MemberError.INVALID_EMAIL.getMessage());
	}

	@Test
	@DisplayName("2글자 미만의 문자열로 회원 등록 시 예외가 발생한다")
	void invalidShortNickname() {
		// given
		String invalidNickname = "x";

		// when & then
		assertThatThrownBy(() -> Member.register("12345", Provider.KAKAO, "test@test.com", invalidNickname))
			.isInstanceOf(DomainException.class)
			.hasMessage(MemberError.INVALID_NICKNAME.getMessage());
	}

	@Test
	@DisplayName("10글자가 넘는 문자열로 회원 등록 시 예외가 발생한다")
	void invalidLongNickname() {
		// given
		String invalidNickname = "veryLongNickname";

		// when & then
		assertThatThrownBy(() -> Member.register("12345", Provider.KAKAO, "test@test.com", invalidNickname))
			.isInstanceOf(DomainException.class)
			.hasMessage(MemberError.INVALID_NICKNAME.getMessage());
	}

	@Test
	@DisplayName("회원 비활성화 시 상태가 변경되고 비활성화 일자가 설정된다")
	void deactivate() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when
		member.deactivate();

		// then
		assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
		assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
	}

	@Test
	@DisplayName("이미 비활성화된 회원을 다시 비활성화하려 할 때 예외가 발생한다")
	void deactivateFail() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when
		member.deactivate();

		// then
		assertThatThrownBy(() -> member.deactivate())
			.isInstanceOf(DomainException.class)
			.hasMessage(MemberError.NOT_ACTIVE.getMessage());
	}

	@Test
	@DisplayName("활성화된 회원의 이메일을 성공적으로 변경한다")
	void updateEmail() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when
		member.updateEmail("new@email.com");

		// then
		assertThat(member.getEmail()).isEqualTo(new Email("new@email.com"));
	}

	@Test
	@DisplayName("비활성화된 회원의 이메일 변경 시 예외가 발생한다")
	void updateEmailFail() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when
		member.deactivate();

		// then
		assertThatThrownBy(() -> member.updateEmail("new@email.com"))
			.isInstanceOf(DomainException.class)
			.hasMessage(MemberError.NOT_ACTIVE.getMessage());
	}

	@Test
	@DisplayName("잘못된 이메일 형식으로 이메일 변경 시 예외가 발생한다")
	void updateInvalidEmail() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when & then
		assertThatThrownBy(() -> member.updateEmail("wrongEmail"))
			.isInstanceOf(DomainException.class)
			.hasMessage(MemberError.INVALID_EMAIL.getMessage());
	}

	@Test
	@DisplayName("활성화된 회원의 닉네임을 성공적으로 변경한다")
	void updateNickname() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when
		member.updateNickname("newName");

		// then
		assertThat(member.getNickname()).isEqualTo("newName");
	}

	@Test
	@DisplayName("비활성화된 회원의 닉네임 변경 시 예외가 발생한다")
	void updateNicknameFail() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when
		member.deactivate();

		// then
		assertThatThrownBy(() -> member.updateNickname("newName"))
			.isInstanceOf(DomainException.class)
			.hasMessage(MemberError.NOT_ACTIVE.getMessage());
	}

	@Test
	@DisplayName("2글자 미만의 문자열로 닉네임 변경 시 예외가 발생한다")
	void updateShortNickname() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when & then
		assertThatThrownBy(() -> member.updateNickname("x"))
			.isInstanceOf(DomainException.class)
			.hasMessage(MemberError.INVALID_NICKNAME.getMessage());
	}

	@Test
	@DisplayName("10글자가 넘는 문자열로 닉네임을 변경 시 예외가 발생한다")
	void updateLongNickname() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when & then
		assertThatThrownBy(() -> member.updateNickname("veryLongNickname"))
			.isInstanceOf(DomainException.class)
			.hasMessage(MemberError.INVALID_NICKNAME.getMessage());
	}
}