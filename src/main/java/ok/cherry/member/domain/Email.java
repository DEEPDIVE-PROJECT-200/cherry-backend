package ok.cherry.member.domain;

import static ok.cherry.member.exception.MemberError.*;

import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ok.cherry.global.exception.error.DomainException;

@Embeddable
public record Email(
	@Column(nullable = false, unique = true)
	String address
) {

	private static final Pattern EMAIL_PATTERN =
		Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,63}$");

	public Email {
		if (address == null) {
			throw new DomainException(INVALID_EMAIL);
		}

		if (!EMAIL_PATTERN.matcher(address).matches()) {
			throw new DomainException(INVALID_EMAIL);
		}
	}
}
