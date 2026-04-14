package com.jetsys.northwindapiserver.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.security")
@Component
@Getter
@Setter
public class SecurityProperties {

	private List<UserDefinition> users = new ArrayList<>();


	@Getter
	@Setter
	public static class UserDefinition {
		private String username;
		private String password;
		private String roles;
	}
}
