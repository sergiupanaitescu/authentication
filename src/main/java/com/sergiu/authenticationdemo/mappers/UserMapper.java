package com.sergiu.authenticationdemo.mappers;

import org.springframework.stereotype.Component;

import com.sergiu.authenticationdemo.dto.UserDTO;
import com.sergiu.authenticationdemo.entities.User;

@Component
public class UserMapper implements Mapper<User, UserDTO>{

	@Override
	public User toEntity(UserDTO dto) {
		//TODO
		return new User();
	}

	@Override
	public UserDTO toDto(User entity) {
		UserDTO dto = new UserDTO();
		dto.setAddress(entity.getAddress());
		dto.setAge(entity.getAge());
		dto.setName(entity.getName());
		dto.setUsername(entity.getUserName());
		return dto;
	}

	
	
}
