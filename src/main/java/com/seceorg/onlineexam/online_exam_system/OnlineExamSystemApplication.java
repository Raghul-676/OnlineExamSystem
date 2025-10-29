package com.seceorg.onlineexam.online_exam_system;

import com.seceorg.onlineexam.online_exam_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineExamSystemApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(OnlineExamSystemApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Initialize roles and default admin user
		userService.initializeRoles();
		userService.createDefaultAdmin();
	}
}
