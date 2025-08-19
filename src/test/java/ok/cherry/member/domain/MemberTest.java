package ok.cherry.member.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MemberTest {
	@Test
	void registerMember() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when & then
		assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
		assertThat(member.getDetail().getRegisteredAt()).isNotNull();
	}

	@Test
	void invalidEmail() {
		// given
		String invalidEmail = "wrongEmail";

		// when & then
		assertThatThrownBy(() -> Member.register("12345", Provider.KAKAO, invalidEmail, "tester"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void invalidShortNickname() {
		// given
		String invalidNickname = "x";

		// when & then
		assertThatThrownBy(() -> Member.register("12345", Provider.KAKAO, "test@test.com", invalidNickname))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void invalidLongNickname() {
		// given
		String invalidNickname = "veryLongNickname";

		// when & then
		assertThatThrownBy(() -> Member.register("12345", Provider.KAKAO, "test@test.com", invalidNickname))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
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
	void deactivateFail() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when
		member.deactivate();

		// then
		assertThatThrownBy(() -> member.deactivate())
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void updateEmail() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when
		member.updateEmail("new@email.com");

		// then
		assertThat(member.getEmail()).isEqualTo(new Email("new@email.com"));
	}

	@Test
	void updateEmailFail() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when
		member.deactivate();

		// then
		assertThatThrownBy(() -> member.updateEmail("new@email.com"))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void updateInvalidEmail() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when & then
		assertThatThrownBy(() -> member.updateEmail("wrongEmail"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void updateNickname() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when
		member.updateNickname("newName");

		// then
		assertThat(member.getNickname()).isEqualTo("newName");
	}

	@Test
	void updateNicknameFail() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when
		member.deactivate();

		// then
		assertThatThrownBy(() -> member.updateNickname("newName"))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void updateInvalidNickname() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");

		// when & then
		assertThatThrownBy(() -> member.updateNickname("x"))
			.isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> member.updateNickname("veryLongNickname"))
			.isInstanceOf(IllegalStateException.class);
	}
}