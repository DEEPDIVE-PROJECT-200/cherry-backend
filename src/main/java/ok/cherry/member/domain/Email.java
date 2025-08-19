package ok.cherry.member.domain;

import java.util.regex.Pattern;

import jakarta.persistence.Embeddable;

@Embeddable
public record Email(String address) {
	private static final Pattern EMAIL_PATTERN =
		Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,63}$");

	public Email {
		if (address == null) {
			throw new IllegalArgumentException("이메일은 null일 수 없습니다");
		}

		if (!EMAIL_PATTERN.matcher(address).matches()) {
			throw new IllegalArgumentException("이메일 형식이 바르지 않습니다: " + address);
		}
	}
}
