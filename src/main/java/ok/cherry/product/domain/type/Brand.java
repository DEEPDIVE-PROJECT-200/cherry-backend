package ok.cherry.product.domain.type;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Brand {
	SONY,
	APPLE,
	BOSE,
	SENNHEISER,
	BANG_OLUFSEN,
	BOWERS_WILKINS,
	MARSHALL,
	DYSON,
	JBL,
	NOTHING;

	public static boolean isValid(String value) {
		if (value == null) {
			return false;
		}

		try {
			Brand.valueOf(value.toUpperCase());
			return true;
		} catch (Exception e) {
			log.error("유효하지 않은 Brand 값입니다: {}", value, e);
			return false;
		}
	}
}
