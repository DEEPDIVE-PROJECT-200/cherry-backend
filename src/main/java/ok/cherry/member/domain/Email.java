package ok.cherry.member.domain;

import static ok.cherry.member.exception.MemberError.*;

import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ok.cherry.global.exception.error.DomainException;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

	private static final Pattern EMAIL_PATTERN =
		Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,63}$");

	@Column(name = "email", nullable = false, unique = true)
	private String address;

	public Email(String address) {
		if (address == null) {
			throw new DomainException(INVALID_EMAIL);
		}

		if (!EMAIL_PATTERN.matcher(address).matches()) {
			throw new DomainException(INVALID_EMAIL);
		}

		this.address = address;
	}

	public String address() {
		return address;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Email email = (Email) obj;
		return address.equals(email.address);
	}

	@Override
	public int hashCode() {
		return address.hashCode();
	}

	@Override
	public String toString() {
		return address;
	}
}
