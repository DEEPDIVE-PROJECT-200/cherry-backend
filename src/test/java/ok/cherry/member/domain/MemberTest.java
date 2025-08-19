package ok.cherry.member.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberTest {
	Member member;

	@BeforeEach
	void setUp() {
		member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");
	}

	@Test
	void registerMember() {
		assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
		assertThat(member.getDetail().getRegisteredAt()).isNotNull();
	}

	@Test
	void invalidEmail() {
		assertThatThrownBy(() -> Member.register("12345", Provider.KAKAO, "wrongEmail", "tester"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void invalidNickname() {
		assertThatThrownBy(() -> Member.register("12345", Provider.KAKAO, "test@test.com", "x"))
			.isInstanceOf(IllegalStateException.class);

		assertThatThrownBy(() -> Member.register("12345", Provider.KAKAO, "test@test.com", "veryLongNickname"))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void deactivate() {
		member.deactivate();

		assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
		assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
	}

	@Test
	void deactivateFail() {
		member.deactivate();

		assertThatThrownBy(() -> member.deactivate())
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void updateEmail() {
		member.updateEmail("new@email.com");

		assertThat(member.getEmail()).isEqualTo(new Email("new@email.com"));
	}

	@Test
	void updateEmailFail() {
		member.deactivate();

		assertThatThrownBy(() -> member.updateEmail("new@email.com"))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void updateInvalidEmail() {
		assertThatThrownBy(() -> member.updateEmail("wrongEmail"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void updateNickname() {
		member.updateNickname("newName");

		assertThat(member.getNickname()).isEqualTo("newName");
	}

	@Test
	void updateNicknameFail() {
		member.deactivate();

		assertThatThrownBy(() -> member.updateNickname("newName"))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void updateInvalidNickname() {
		assertThatThrownBy(() -> member.updateNickname("x"))
			.isInstanceOf(IllegalStateException.class);

		assertThatThrownBy(() -> member.updateNickname("veryLongNickname"))
			.isInstanceOf(IllegalStateException.class);
	}
}