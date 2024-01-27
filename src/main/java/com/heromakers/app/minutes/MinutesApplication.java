package com.heromakers.app.minutes;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class MinutesApplication {
	@PostConstruct
	public void started() {
		// timezone for Korea
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(MinutesApplication.class, args);
	}

}
