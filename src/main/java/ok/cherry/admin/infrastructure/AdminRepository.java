package ok.cherry.admin.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ok.cherry.admin.domain.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

	Optional<Admin> findByUsername(String username);
}

