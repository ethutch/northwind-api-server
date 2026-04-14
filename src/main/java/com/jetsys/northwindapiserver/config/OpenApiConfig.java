package com.jetsys.northwindapiserver.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
public class OpenApiConfig {

	@Bean
	@Profile("openapi-gen")
	public OpenApiCustomizer openApiCustomizer() {
		return openApi -> {
			// Replace the auto-detected server (with port 18080) with the correct ones
			openApi.setServers(List.of(
					new Server()
							.url("http://localhost:8080")
							.description("Local development server"),

					new Server()
							.url("https://api.yourcompany.com")   // ← change to your real production URL
							.description("Production server (Kubernetes)")
			));
		};
	}

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Northwind API")
						.version("v1"))
				.components(new Components()
						.addSecuritySchemes("bearerAuth",
								new SecurityScheme()
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement()
						.addList("bearerAuth"));
	}
}