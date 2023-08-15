package com.example.PiB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class PiBApplication {

	public static void main(String[] args) {
		SpringApplication.run(PiBApplication.class, args);
	}

}
