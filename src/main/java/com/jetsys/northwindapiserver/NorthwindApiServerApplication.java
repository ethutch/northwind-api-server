package com.jetsys.northwindapiserver;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@SpringBootApplication
public class NorthwindApiServerApplication {

	public static final Marker SECURITY = MarkerManager.getMarker("SECURITY");

	public static void main(String[] args) {
		SpringApplication.run(NorthwindApiServerApplication.class, args);
	}

}
