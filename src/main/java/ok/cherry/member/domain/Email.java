package ok.cherry.member.domain;

import java.util.regex.Pattern;

import jakarta.persistence.Embeddable;

@Embeddable
public record Email(String address) {
	private final static Pattern EMAIL_PATTERN =
		Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

	public Email {
		if (!EMAIL_PATTERN.matcher(address).matches()) {
			throw new IllegalArgumentException("이메일 형식이 바르지 않습니다: " + address);
		}
	}
}
