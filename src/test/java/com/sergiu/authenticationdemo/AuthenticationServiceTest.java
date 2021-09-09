package com.sergiu.authenticationdemo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sergiu.authenticationdemo.cache.JwtInvalidationCache;
import com.sergiu.authenticationdemo.dto.UserDTO;
import com.sergiu.authenticationdemo.entities.User;
import com.sergiu.authenticationdemo.exceptions.LoginException;
import com.sergiu.authenticationdemo.mappers.UserMapper;
import com.sergiu.authenticationdemo.repository.UserRepository;
import com.sergiu.authenticationdemo.service.AuthenticationService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthenticationServiceTest {
	
	private String secretKey = "RgUkXp2s5v8y/B?D(G+KbPeShVmYq3t6w9z$C&F)H@McQfTjWnZr4u7x!A%D*G-K";
	
	private UserRepository userRepository = Mockito.mock(UserRepository.class);
	
	private JwtInvalidationCache invalidationCache;
	
	private AuthenticationService authService;
	
	private User user;
	
	
	@BeforeEach
	void initCache() {
		invalidationCache = new JwtInvalidationCache(secretKey);
		authService = new AuthenticationService(secretKey, userRepository, new UserMapper(), invalidationCache);
		user = new User();
		user.setUserName("theUser");
		user.setPassword("0ed4768c11461ab762da1f5719ad560b"); //thePassword in md5
	}
	
	@Test
	void testLoginWrongPass() {	
		Mockito.when(userRepository.findByUserName("theUser")).thenReturn(Optional.ofNullable(user));
		LoginException exception = assertThrows(LoginException.class, () -> {
			authService.login("theUser", "passTest");
		});
		assertTrue(exception.getMessage().contains("Password did not match"));
	}
	
	@Test
	void testLoginCorrectPass() {	
		Mockito.when(userRepository.findByUserName("theUser")).thenReturn(Optional.ofNullable(user));
		UserDTO user = authService.login("theUser", "thePassword");
		assertNotNull(user.getToken());
	}
	
	@Test
	void testLoginWrongUser() {	
		Mockito.when(userRepository.findByUserName(AdditionalMatchers.not(Matchers.eq("theUser")))).thenReturn(Optional.ofNullable(null));
		LoginException exception = assertThrows(LoginException.class, () -> {
			authService.login("xxx", "thePassword");
		});
		assertTrue(exception.getMessage().contains("User is not registered"));
	}
	
	@Test
	void  testBlacklistAtLogout() {
		Mockito.when(userRepository.findByUserName("theUser")).thenReturn(Optional.ofNullable(user));
		UserDTO user = authService.login("theUser", "thePassword");
		assertNotNull(user.getToken());
		String strippedToken = user.getToken().replace("Bearer ", "");
		authService.logout(strippedToken);
		assertTrue(invalidationCache.isTokenBlackListed(strippedToken));
	}

	
}
