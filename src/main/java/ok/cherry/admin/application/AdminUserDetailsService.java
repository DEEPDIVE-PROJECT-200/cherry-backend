package ok.cherry.admin.application;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ok.cherry.admin.domain.Admin;
import ok.cherry.admin.exception.AdminError;
import ok.cherry.admin.infrastructure.AdminRepository;
import ok.cherry.global.exception.error.BusinessException;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException(AdminError.ADMIN_NOT_FOUND));
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(ROLE_ADMIN));
        return new User(
            admin.getUsername(),
            admin.getPassword(),
            true,
            true,
            true,
            true,
            authorities
        );
    }
}

