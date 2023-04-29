package com.agileavengers.icuconnectbackend.service;

import java.util.ArrayList;
import java.util.Optional;

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

	public JwtUserDetailsService(UserRepository userRepository, UserMapper userMapper) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optionalUser = userRepository.findByUsername(username);
		if (optionalUser.isEmpty()) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}

        User user = optionalUser.get();

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), new ArrayList<>());
    }

	public User saveUser(RegisterUserDto registerUserDto) throws ResponseStatusException {
		if (userRepository.findByUsername(registerUserDto.getUsername()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken.");
		}
		if (userRepository.findByEmail(registerUserDto.getEmail()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already taken.");
		}
		User newUser = userMapper.fromDto(registerUserDto);
		return userRepository.save(newUser);
	}
}
