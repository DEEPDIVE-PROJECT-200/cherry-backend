package ok.cherry;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;

import org.assertj.core.api.AssertProvider;
import org.springframework.test.json.JsonPathValueAssert;

public class AssertThatUtils {

	public static Consumer<AssertProvider<JsonPathValueAssert>> notNull() {
		return value -> assertThat(value).isNotNull();
	}

	public static Consumer<AssertProvider<JsonPathValueAssert>> equalsTo(String providerId) {
		return value -> assertThat(value).isEqualTo(providerId);
	}

	public static Consumer<AssertProvider<JsonPathValueAssert>> isTrue() {
		return value -> assertThat(value).asBoolean().isTrue();
	}

	public static Consumer<AssertProvider<JsonPathValueAssert>> isFalse() {
		return value -> assertThat(value).asBoolean().isFalse();
	}
}

