package com.sergiu.authenticationdemo.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sergiu.authenticationdemo.dto.UserDTO;
import com.sergiu.authenticationdemo.service.AuthenticationService;

@RestController
@RequestMapping(path = "api/v1/authentication")
public class AuthenticationController {

	private AuthenticationService authService;

	public AuthenticationController(AuthenticationService authService) {
		super();
		this.authService = authService;
	}

	@PostMapping("/login")
	public UserDTO login(@RequestParam("user") String username, @RequestParam("password") String password) {

		return authService.login(username, password);

	}
}
