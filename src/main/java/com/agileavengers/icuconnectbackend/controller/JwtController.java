package com.agileavengers.icuconnectbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.agileavengers.icuconnectbackend.config.JwtTokenUtil;
import com.agileavengers.icuconnectbackend.model.dto.JwtRequestDto;
import com.agileavengers.icuconnectbackend.model.dto.JwtResponseDto;
import com.agileavengers.icuconnectbackend.model.dto.RegisterUserDto;
import com.agileavengers.icuconnectbackend.service.JwtUserDetailsService;

import jakarta.validation.Valid;

// TODO: More detailed error handling
@RestController
@CrossOrigin
public class JwtController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtRequestDto authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getName(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getName());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponseDto(token));
    }

    @PostMapping("/users")
    public ResponseEntity<?> saveUser(@Valid @RequestBody RegisterUserDto userDto) throws Exception {
        try {
            userDetailsService.saveUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Name/Email already taken!");
        }
    }

    private void authenticate(String name, String password) throws Exception {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(name, password));
    }
}
