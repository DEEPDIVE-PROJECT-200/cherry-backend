package ok.cherry.member.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ok.cherry.global.exception.error.DomainException;
import ok.cherry.member.exception.MemberError;

class EmailTest {

	@Test
	@DisplayName("잘못된 이메일 형식으로 Email 객체 생성 시 예외가 발생한다")
	void createEmailFail() {
		// given
		String wrongAddress = "wrongAddress";

		// when & then
		assertThatThrownBy(() -> new Email(wrongAddress))
			.isInstanceOf(DomainException.class)
			.hasMessage(MemberError.INVALID_EMAIL.getMessage());
	}

	@Test
	@DisplayName("같은 이메일 주소를 가진 Email 객체들은 동등하다")
	void equality() {
		// give
		var email1 = new Email("test@test.com");
		var email2 = new Email("test@test.com");

		// when & then
		assertThat(email1).isEqualTo(email2);
	}
}