package ok.cherry.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainerConfig {

	private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(
		DockerImageName.parse("redis:7-alpine"))
		.withExposedPorts(6379);

	static {
		REDIS_CONTAINER.start();
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
		registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
	}
}