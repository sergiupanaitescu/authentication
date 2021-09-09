package com.sergiu.authenticationdemo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.sergiu.authenticationdemo.cache.JwtInvalidationCache;
import com.sergiu.authenticationdemo.dto.UserDTO;
import com.sergiu.authenticationdemo.entities.User;
import com.sergiu.authenticationdemo.mappers.UserMapper;
import com.sergiu.authenticationdemo.repository.UserRepository;
import com.sergiu.authenticationdemo.service.AuthenticationService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthenticationLogoutAccesTest {

	private String secretKey = "RgUkXp2s5v8y/B?D(G+KbPeShVmYq3t6w9z$C&F)H@McQfTjWnZr4u7x!A%D*G-K";

	private UserRepository userRepository = Mockito.mock(UserRepository.class);

	private JwtInvalidationCache invalidationCache;

	private AuthenticationService authService;

	private User user;

	private MockMvc mvc;

	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
		invalidationCache = new JwtInvalidationCache(secretKey);
		authService = new AuthenticationService(secretKey, userRepository, new UserMapper(), invalidationCache);
		user = new User();
		user.setUserName("theUser");
		user.setPassword("0ed4768c11461ab762da1f5719ad560b"); // thePassword in md5
	}

	@Test
	void givenInvalidTokenFailWith401() throws Exception {
		mvc.perform(post("/api/v1/authentication/logout").header("Authorization", String.format("Bearer xxxxx")))
				.andExpect(status().is(403));
	}

	@Test
	void givenNoTokenFailWith401() throws Exception {
		mvc.perform(post("/api/v1/authentication/logout").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(401));
	}

	@Test
	void givenValidTokenRespondWith200And403AtTokenReuse() throws Exception {
		Mockito.when(userRepository.findByUserName("theUser")).thenReturn(Optional.ofNullable(user));
		UserDTO user = authService.login("theUser", "thePassword");
		assertNotNull(user.getToken());
		mvc.perform(post("/api/v1/authentication/logout").header("Authorization",
				String.format("Bearer %s", user.getToken()))).andExpect(status().is(200));
		mvc.perform(post("/api/v1/user/details").header("Authorization", String.format("Bearer %s", user.getToken())))
				.andExpect(status().is(401)); // no 403 but 401 because the blacklisted token is removed from request
	}

}
