package ok.cherry.admin.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import ok.cherry.admin.domain.Admin;
import ok.cherry.admin.infrastructure.AdminRepository;

@Slf4j
@Configuration
@Profile({"prod"})
public class AdminAccountInitializer implements ApplicationRunner {

	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;
	private final String adminUsername;
	private final String adminPassword;

	public AdminAccountInitializer(
		AdminRepository adminRepository,
		PasswordEncoder passwordEncoder,
		@Value("${admin.username}") String adminUsername,
		@Value("${admin.password}") String adminPassword
	) {
		this.adminRepository = adminRepository;
		this.passwordEncoder = passwordEncoder;
		this.adminUsername = adminUsername;
		this.adminPassword = adminPassword;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (adminRepository.findByUsername(adminUsername).isEmpty()) {
			adminRepository.save(new Admin(adminUsername, passwordEncoder.encode(adminPassword)));
		}
	}
}
