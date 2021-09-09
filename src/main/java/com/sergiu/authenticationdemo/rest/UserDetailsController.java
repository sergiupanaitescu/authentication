package com.sergiu.authenticationdemo.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sergiu.authenticationdemo.dto.UserDTO;
import com.sergiu.authenticationdemo.service.UserDetailsService;

@RestController
@RequestMapping(path = "/api/v1/user/details")
public class UserDetailsController {

	private UserDetailsService detailsService;

	public UserDetailsController(UserDetailsService detailsService) {
		this.detailsService = detailsService;
	}

	@PostMapping
	public UserDTO getUserDetails(@RequestParam String username) {
		return detailsService.getuserDetailsByUserName(username);
	}

}
