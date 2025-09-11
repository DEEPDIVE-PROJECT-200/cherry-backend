package ok.cherry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableScheduling
@SpringBootApplication
@EnableWebSecurity(debug = true)
public class CherryApplication {

	public static void main(String[] args) {
		SpringApplication.run(CherryApplication.class, args);
	}

}
