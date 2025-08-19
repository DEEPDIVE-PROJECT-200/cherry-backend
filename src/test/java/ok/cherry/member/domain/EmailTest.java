package ok.cherry.member.domain;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailTest {
	@Test
	void createEmailFail() {
		// given
		String wrongAddress = "wrongAddress";

		// when & then
		assertThatThrownBy(() -> new Email(wrongAddress))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void equality() {
		// give
		var email1 = new Email("test@test.com");
		var email2 = new Email("test@test.com");

		// when & then
		assertThat(email1).isEqualTo(email2);
	}
}