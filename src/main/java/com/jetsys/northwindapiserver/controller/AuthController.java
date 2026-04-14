package com.jetsys.northwindapiserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface AuthController {
	// Very basic login for now — replace with real credentials/auth logic later
	@PostMapping("/login")
	Map<String, String> login(@RequestBody Map<String, String> request);
}
