package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.RegisterUserDto;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder bcryptEncoder;

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

	public void saveUser(RegisterUserDto user) throws Exception {
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new Exception();
		}
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new Exception();
		}
		User newUser = new User();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setEmail(user.getEmail());
		userRepository.save(newUser);
	}
}
