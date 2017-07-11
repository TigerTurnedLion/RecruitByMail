package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		Conductor conductor = Conductor.getInstance();
		conductor.ProcessEmails();
		//conductor.ProcessEmails_Test();

	}
}
