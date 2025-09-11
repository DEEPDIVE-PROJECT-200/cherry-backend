package ok.cherry.admin.application;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ok.cherry.admin.domain.Admin;
import ok.cherry.admin.infrastructure.AdminRepository;

@Configuration
@RequiredArgsConstructor
@Transactional
@Profile({"local", "dev"})
public class AdminDataInitializer implements CommandLineRunner {

	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		if (adminRepository.findByUsername("admin").isEmpty()) {
			adminRepository.save(new Admin("admin", passwordEncoder.encode("admin")));
		}
	}
}