package io.repliforce.RepliforceJsonValidator;

import io.repliforce.RepliforceJsonValidator.configs.ExcludeFromCoverageGeneratedReport;
import io.repliforce.RepliforceJsonValidator.domain.Role;
import io.repliforce.RepliforceJsonValidator.domain.User;
import io.repliforce.RepliforceJsonValidator.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@ExcludeFromCoverageGeneratedReport
@SpringBootApplication
public class RepliforceJsonValidatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepliforceJsonValidatorApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner runner(UserService userService) {
		return args -> {
			userService.saveRole(new Role(null, "ROLE_HUNTER"));
			userService.saveRole(new Role(null, "ROLE_NAVIGATOR"));
			userService.saveRole(new Role(null, "ROLE_LEADER"));
			userService.saveRole(new Role(null, "ROLE_COMMANDER"));

			userService.saveUser(new User(null, "X", "mmx", "TestX", new ArrayList<>()));
			userService.saveUser(new User(null, "Zero", "zero", "TestZero", new ArrayList<>()));
			userService.saveUser(new User(null, "Axl", "axl", "TestAxl", new ArrayList<>()));
			userService.saveUser(new User(null, "Iris", "iris", "TestIris", new ArrayList<>()));

			userService.addRoleToUser("mmx", "ROLE_HUNTER");
			userService.addRoleToUser("mmx", "ROLE_COMMANDER");
			userService.addRoleToUser("mmx", "ROLE_LEADER");
			userService.addRoleToUser("zero", "ROLE_HUNTER");
			userService.addRoleToUser("zero", "ROLE_LEADER");
			userService.addRoleToUser("axl", "ROLE_HUNTER");
			userService.addRoleToUser("iris", "ROLE_NAVIGATOR");
		};
	}

}
