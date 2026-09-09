package com.lonelys.Dubb;

import com.lonelys.Dubb.entity.User;
import com.lonelys.Dubb.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DubbApplication {

	public static void main(String[] args) {
		SpringApplication.run(DubbApplication.class, args);
	}

	@Bean
    CommandLineRunner testUserService(UserService userService) {
		return args -> {
			User u = userService.createUser("mathieu", "mathieu@mdg.com");
			System.out.println("Créé : " + u);
		};
	}

}
