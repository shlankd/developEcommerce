package com.example.ecommerce;

import com.example.ecommerce.service.InitiateRolesUsersAndAdminService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(InitiateRolesUsersAndAdminService initiateRolesUsersAndAdminService){
		return args -> {
			// Initiates roles, admin and users.
			initiateRolesUsersAndAdminService.initiateRolesUsersAndAdmin();
		};
	}

}
