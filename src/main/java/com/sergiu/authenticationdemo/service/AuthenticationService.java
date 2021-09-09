package com.sergiu.authenticationdemo.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

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

	@Value("${secretKey}")
	private String secretKey;

	private UserRepository userRepo;

	private UserMapper userMapper;

	private JwtInvalidationCache invalidationcache;

	public AuthenticationService(UserRepository userRepo, UserMapper userMapper,
			JwtInvalidationCache invalidationcache) {
		this.userRepo = userRepo;
		this.userMapper = userMapper;
		this.invalidationcache = invalidationcache;
	}

	public UserDTO login(String username, String password) {
		Optional<User> userOptional = userRepo.findByUserName(username);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();// TODO log
				throw new InternalServerError();// TODO maybe error message
			}
			md.update(password.getBytes());
			byte[] digest = md.digest();
			String passwordHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
			if (passwordHash.equalsIgnoreCase(user.getPassword())) {
				UserDTO loggedUser = userMapper.toDto(user);
				loggedUser.setToken(getJWTToken(user));
				return loggedUser;
			} else {
				throw new LoginException();
			}

		}
		throw new LoginException();
	}

	private String getJWTToken(User user) {
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(
				user.getRoles().stream().map(Object::toString).collect(Collectors.joining(",")));

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
	}
}
