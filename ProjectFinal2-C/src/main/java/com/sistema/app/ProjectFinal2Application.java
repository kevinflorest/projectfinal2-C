package com.sistema.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ProjectFinal2Application {

	public static void main(String[] args) {
		SpringApplication.run(ProjectFinal2Application.class, args);
	}

}
