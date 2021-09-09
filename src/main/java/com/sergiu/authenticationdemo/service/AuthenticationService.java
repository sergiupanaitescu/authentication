package com.sergiu.authenticationdemo.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import com.sergiu.authenticationdemo.cache.JwtInvalidationCache;
import com.sergiu.authenticationdemo.dto.UserDTO;
import com.sergiu.authenticationdemo.entities.User;
import com.sergiu.authenticationdemo.exceptions.InternalServerError;
import com.sergiu.authenticationdemo.exceptions.LoginException;
import com.sergiu.authenticationdemo.mappers.UserMapper;
import com.sergiu.authenticationdemo.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class AuthenticationService {

	Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	private String secretKey;

	private UserRepository userRepo;

	private UserMapper userMapper;

	private JwtInvalidationCache invalidationcache;

	public AuthenticationService(@Value("${secretKey}") String secretKey, UserRepository userRepo,
			UserMapper userMapper, JwtInvalidationCache invalidationcache) {
		this.userRepo = userRepo;
		this.userMapper = userMapper;
		this.invalidationcache = invalidationcache;
		this.secretKey = secretKey;
	}

	public UserDTO login(String username, String password) {
		Optional<User> userOptional = userRepo.findByUserName(username);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				logger.error("Password hashing algorithm error!");
				throw new InternalServerError();
			}
			md.update(password.getBytes());
			byte[] digest = md.digest();
			String passwordHash = DatatypeConverter.printHexBinary(digest);
			if (passwordHash.equalsIgnoreCase(user.getPassword())) {
				UserDTO loggedUser = userMapper.toDto(user);
				loggedUser.setToken(getJWTToken(user));
				logger.info(String.format("User: %s was logged in succesfully", username));
				return loggedUser;
			} else {
				String userPassError = String.format("User: %s failed to log in! Password did not match", username);
				logger.info(userPassError);
				throw new LoginException(userPassError);
			}

		}
		throw new LoginException(String.format("User: %s failed to log in! User is not registered!", username));
	}

	private String getJWTToken(User user) {
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList(!user.getRoles().isEmpty()
						? user.getRoles().stream().map(Object::toString).collect(Collectors.joining(","))
						: "");

		String token = Jwts.builder().setId("jwtCrazyToken").setSubject(user.getUserName())
				.claim("authorities",
						grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 600000))
				.signWith(SignatureAlgorithm.HS512, secretKey.getBytes()).compact();

		return "Bearer " + token;
	}

	public void logout(String token) {
		invalidationcache.blackList(token);
		logger.info(String.format("Token: %s was blacklisted because of user log out", token));
	}
}
