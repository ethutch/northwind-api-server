package com.jetsys.northwindapiserver.controller;

import com.jetsys.northwindapiserver.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController {

	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;


	@Override
	@PostMapping("/login")
	public Map<String, String> login(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");

		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, password)
			);
		} catch (BadCredentialsException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}

		UserDetails user = (UserDetails) authentication.getPrincipal();

		List<String> roles = user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.map(r -> r.replace("ROLE_", ""))
				.toList();

		String token = jwtService.generateToken(username, roles);

		return Map.of("token", token);
	}
}