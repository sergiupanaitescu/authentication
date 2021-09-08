package com.sergiu.authenticationdemo.service;

import java.util.Optional;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Service;

import com.sergiu.authenticationdemo.dto.UserDTO;
import com.sergiu.authenticationdemo.entities.User;
import com.sergiu.authenticationdemo.mappers.UserMapper;
import com.sergiu.authenticationdemo.repository.UserRepository;

@Service
public class UserDetailsService {

	private UserRepository userRepo;

	private UserMapper userMapper;

	public UserDetailsService(UserRepository userRepo, UserMapper userMapper) {
		this.userRepo = userRepo;
		this.userMapper = userMapper;
	}

	public UserDTO getuserDetailsByUserName(String username) {
		Optional<User> user = userRepo.findByUserName(username);
		if (user.isPresent()) {
			return userMapper.toDto(user.get());
		}
		throw new NoResultException();

	}
}
