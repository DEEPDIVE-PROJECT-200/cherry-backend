package ok.cherry.member.domain;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailTest {

	@Test
	void createEmailFail() {
		assertThatThrownBy(() -> new Email("wrongAddress"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void equality() {
		var email1 = new Email("test@test.com");
		var email2 = new Email("test@test.com");

		assertThat(email1).isEqualTo(email2);
	}
}