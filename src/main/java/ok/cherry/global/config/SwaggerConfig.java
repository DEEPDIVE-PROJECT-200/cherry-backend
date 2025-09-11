package ok.cherry.global.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class SwaggerConfig {

	private static final String JWT_SCHEME_NAME = "JWT";
	private static final String BEARER_FORMAT = "JWT";
	private static final String SCHEME = "bearer";

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.components(new Components()
				.addSecuritySchemes(JWT_SCHEME_NAME, createJWTSecurityScheme())
			)
			.info(createApiInfo())
			.servers(createServers())
			.tags(createTags());
	}

	/**
	 * API 기본 정보 설정
	 */
	private Info createApiInfo() {
		return new Info()
			.title("Cherry Backend API")
			.description("Cherry REST API 문서 - 구름 백엔드 개발자 과정 3기 4차 합반 프로젝트")
			.version("v1.0.0")
			.contact(new Contact()
				.name("200ok Cherry Team")
				.email("kdasunb6@gmail.com")
				.url("https://github.com/DEEPDIVE-PROJECT-200/cherry-backend")
			)
			.license(new License()
				.name("MIT License")
				.url("https://opensource.org/licenses/MIT")
			);
	}

	/**
	 * JWT 보안 스키마 설정
	 */
	private SecurityScheme createJWTSecurityScheme() {
		return new SecurityScheme()
			.name(JWT_SCHEME_NAME)
			.type(SecurityScheme.Type.HTTP)
			.scheme(SCHEME)
			.bearerFormat(BEARER_FORMAT)
			.description("JWT 토큰을 입력하세요 (Bearer 제외)");
	}

	/**
	 * API 서버 정보 설정
	 */
	private List<Server> createServers() {
		return Arrays.asList(
			new Server()
				.url("http://localhost:8080")
				.description("로컬 개발 서버"),
			new Server()
				.url("https://localhost:8080")
				.description("개발 서버"),
			new Server()
				.url("https://server.200cherry.shop")
				.description("운영 서버")
		);
	}

	/**
	 * API 태그 그룹 설정
	 */
	private List<Tag> createTags() {
		return Arrays.asList(
			new Tag()
				.name("Authentication")
				.description("🔐 인증 API - 회원 로그인, 회원가입, 토큰 관리 API"),
			new Tag()
				.name("Members")
				.description("👤 회원 API - 회원 정보 조회, 수정, 탈퇴, 관리 API"),
			new Tag()
				.name("Carts")
				.description("🛒 장바구니 API - 장바구니 조회, 관리 API"),
			new Tag()
				.name("Products")
				.description("🎧 상품 API - 상품 조회, 등록, 수정, 삭제 API"),
			new Tag()
				.name("Rentals")
				.description("🍒 대여 API - 상품 대여, 체험, 회수 API"),
			new Tag()
				.name("Payments")
				.description("💳 결제 API - 결제 처리, 환불, 결제 내역 API"),
			new Tag()
				.name("Shipping")
				.description("📦 배송 API - 배송 처리, 반납, 배송 내역 API"),
			new Tag()
				.name("Statistics")
				.description("📊 통계 API - 이용 통계, 매출 통계 API"),
			new Tag()
				.name("Dev")
				.description("🔧 개발 도구 - 개발용 테스트 API (개발 환경 전용)")
		);
	}
}
