package ok.cherry.rental.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RentalNumberGenerator {

	private static final SecureRandom random = new SecureRandom();

	public static String generate() {
		String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));

		int randomNumber = random.nextInt(100000000);
		String numericSuffix = String.format("%08d", randomNumber);

		return "CH" + dateTime + numericSuffix;
	}
}
