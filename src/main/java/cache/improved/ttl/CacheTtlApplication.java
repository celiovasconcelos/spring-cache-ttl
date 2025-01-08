package cache.improved.ttl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CacheTtlApplication {

	public static void main(String[] args) {
		SpringApplication.run(CacheTtlApplication.class, args);
	}

}
