package com.agileavengers.icuconnectbackend.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.RegisterUserDto;
import com.agileavengers.icuconnectbackend.repository.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		Optional<User> optionalUser = userRepository.findByName(name);
		if (optionalUser.isEmpty()) {
			throw new UsernameNotFoundException("User not found with name: " + name);
		}

		User user = optionalUser.get();

		return new org.springframework.security.core.userdetails.User(user.getName(),
				user.getPassword(), new ArrayList<>());
	}

	public void saveUser(RegisterUserDto user) throws Exception {
		if (userRepository.findByName(user.getName()).isPresent()) {
			throw new Exception();
		}
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new Exception();
		}
		User newUser = new User();
		newUser.setName(user.getName());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setEmail(user.getEmail());
		userRepository.save(newUser);
	}
}
