package ok.cherry.product.domain.type;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Color {
	BLACK,
	WHITE,
	PLATINUM_SILVER,
	MIDNIGHT_BLUE,
	LUNA_BLUE,
	WHITE_SMOKE,
	SANDSTONE,
	BLUE,
	MIDNIGHT,
	STARLIGHT,
	PURPLE,
	ORANGE,
	GRAPHITE,
	CHAMPAGNE,
	TIMBER,
	GOLD,
	ROYAL_BURGUNDY,
	DARK_FOREST,
	DEEP_PLUM,
	COPPER,
	BLACK_NICKEL,
	CERAMIC_CINNABAR,
	TAN;

	public static boolean isValid(String value) {
		if (value == null) {
			return false;
		}

		try {
			Color.valueOf(value);
			return true;
		} catch (Exception e) {
			log.error("유효하지 않은 Color 값입니다: {}", value, e);
			return false;
		}
	}

}
