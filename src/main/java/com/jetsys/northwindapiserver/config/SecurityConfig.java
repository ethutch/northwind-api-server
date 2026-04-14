package com.jetsys.northwindapiserver.config;

import com.jetsys.northwindapiserver.security.AppUserDetailsService;
import com.jetsys.northwindapiserver.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;

	public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
		this.jwtAuthFilter = jwtAuthFilter;
	}


	/**
	 * This is for Prometheus only.  It is to secure the actuator endpoint
	 * @return  SecurityFilterChain The filter chain
	 */
	@Bean
	@Order(1)
	public SecurityFilterChain actuatorFilterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {
		http
				.authenticationProvider(authenticationProvider)
				.securityMatcher(EndpointRequest.toAnyEndpoint())
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/error").permitAll()
						// 1. Requirement: Health is public
						.requestMatchers(EndpointRequest.to("health")).permitAll()

						// 2. Requirement: Prometheus and Info secured via role
						// This handles the "ACTUATOR" to "ROLE_ACTUATOR" mapping automatically
						.requestMatchers(EndpointRequest.to("prometheus", "info", "beans")).hasRole("ACTUATOR")

						// 3. Close the door on any other actuator endpoints you haven't specified
						.anyRequest().denyAll()
				)
				.httpBasic(Customizer.withDefaults())
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}




	@Bean
	@Order(2)
	SecurityFilterChain apiChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {
		http
				.authenticationProvider(authenticationProvider)
				.csrf(csrf -> csrf.disable())
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/error").permitAll()
						.requestMatchers("/auth/login").permitAll()
						.requestMatchers("/api/v1/**").authenticated()
						.requestMatchers(
								"/v3/api-docs/**",
								"/v3/api-docs.yaml",
								"/swagger-ui/**",
								"/swagger-ui.html"
						).permitAll()
						.anyRequest().denyAll()
				)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.httpBasic(httpBasic -> httpBasic.disable())
				.formLogin(form -> form.disable());

		return http.build();
	}


	// This stops JwtAuthFilter from jumping into the Actuator chain
	@Bean
	public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegistration(JwtAuthFilter filter) {
		FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setEnabled(false); // Keeps it out of the global filter list
		return registration;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider(
			AppUserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}

