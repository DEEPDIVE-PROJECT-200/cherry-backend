package ok.cherry.admin.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.admin.domain.Admin;
import ok.cherry.admin.infrastructure.AdminRepository;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Transactional
@Profile({"prod"})
public class AdminAccountInitializer implements ApplicationRunner {

	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${admin.username}")
	private String adminUsername;
	@Value("${admin.password}")
	private String adminPassword;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (adminRepository.findByUsername(adminUsername).isEmpty()) {
			String encodedPassword = passwordEncoder.encode(adminPassword);
			Admin admin = new Admin(adminUsername, encodedPassword);
			adminRepository.save(admin);
			log.info("Admin account created: {}", adminUsername);
		}

	}
}
