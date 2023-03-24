package com.agileavengers.icuconnectbackend.service.implementation;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.agileavengers.icuconnectbackend.mapper.UserMapper;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.RegisterUserDto;
import com.agileavengers.icuconnectbackend.repository.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	private UserRepository userRepository;

	private final UserMapper userMapper;

	@Autowired
	JwtUserDetailsService(UserRepository userRepository, UserMapper userMapper) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		Optional<User> optionalUser = userRepository.findByUsername(name);
		if (optionalUser.isEmpty()) {
			throw new UsernameNotFoundException("User not found with name: " + name);
		}

		User user = optionalUser.get();

		return new org.springframework.security.core.userdetails.User(user.getUsername(),
				user.getPassword(), new ArrayList<>());
	}

	public User saveUser(RegisterUserDto userDto) throws ResponseStatusException {
		if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken.");
		}
		if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already taken.");
		}
		User newUser = userMapper.fromDto(userDto);

		return userRepository.save(newUser);
	}
}
