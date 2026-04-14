package com.jetsys.northwindapiserver.security;

import com.jetsys.northwindapiserver.config.SecurityProperties;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

	private final SecurityProperties securityProperties;

	public AppUserDetailsService(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return securityProperties.getUsers().stream()
				.filter(u -> u.getUsername().equals(username))
				.findFirst()
				.map(u -> User.withUsername(u.getUsername())
						.password(u.getPassword())
						.roles(u.getRoles())
						.build())
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}
}
