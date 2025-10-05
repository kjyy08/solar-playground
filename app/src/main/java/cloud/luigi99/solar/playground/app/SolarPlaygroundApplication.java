package cloud.luigi99.solar.playground.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "cloud.luigi99.solar.playground")
@EnableJpaRepositories(basePackages = "cloud.luigi99.solar.playground")
@EntityScan(basePackages = "cloud.luigi99.solar.playground")
public class SolarPlaygroundApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolarPlaygroundApplication.class, args);
	}

}
