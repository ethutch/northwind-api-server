package com.jetsys.northwindapiserver.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtParser;

import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

	// MUST be at least 256‑bit (Base64 encoded) for HS256
	private static final String SECRET_B64 = "12345678901234567890123456789012345678901234567890";
	private final SecretKey key;

	public JwtService() {
		byte[] keyBytes = java.util.Base64.getDecoder().decode(SECRET_B64);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(String username, List<String> roles) {
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(username)
				.claim("roles", roles)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
				.signWith(key)        // HS256 by default
				.compact();
	}

	public String validateTokenAndGetUsername(String token) {
		try {
			JwtParser parser = Jwts.parser()
					.verifyWith(key)   // correct for 0.12.x
					.build();

			Jws<Claims> jws = parser.parseSignedClaims(token);
			Claims claims = jws.getPayload();
			return claims.getSubject();
		} catch (Exception e) {
			// token invalid (signature, expired, etc.)
			return null;
		}
	}

	public List<String> extractRoles(String token) {
		try {
			Jws<Claims> jws = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token);
			Claims claims = jws.getPayload();
			// Returns empty list if roles claim is missing
			return claims.get("roles", List.class) != null ? claims.get("roles", List.class) : List.of();
		} catch (Exception e) {
			return List.of();
		}
	}

}
