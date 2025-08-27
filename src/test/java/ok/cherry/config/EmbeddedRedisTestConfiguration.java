package ok.cherry.config;

import java.io.IOException;
import java.net.ServerSocket;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

@TestConfiguration
public class EmbeddedRedisTestConfiguration {

	private int redisPort;
	private RedisServer redisServer;

	@PostConstruct
	public void startRedis() throws IOException {
		redisPort = findAvailablePort();
		redisServer = RedisServer.newRedisServer()
			.port(redisPort)
			.build();
		redisServer.start();
	}

	@PreDestroy
	public void stopRedis() throws IOException {
		if (redisServer != null && redisServer.isActive()) {
			redisServer.stop();
		}
	}

	@Bean
	@Primary
	public RedisConnectionFactory testRedisConnectionFactory() {
		LettuceConnectionFactory factory = new LettuceConnectionFactory("localhost", redisPort);
		factory.setValidateConnection(false);
		factory.afterPropertiesSet();
		return factory;
	}

	/**
	 * 사용 가능한 포트를 찾는 메서드
	 */
	private int findAvailablePort() throws IOException {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		}
	}
}