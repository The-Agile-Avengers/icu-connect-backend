package com.agileavengers.icuconnectbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.agileavengers.icuconnectbackend.config.JwtTokenUtil;
import com.agileavengers.icuconnectbackend.model.dto.JwtRequestDto;
import com.agileavengers.icuconnectbackend.model.dto.JwtResponseDto;
import com.agileavengers.icuconnectbackend.model.dto.RegisterUserDto;
import com.agileavengers.icuconnectbackend.service.implementation.JwtUserDetailsService;

import jakarta.validation.Valid;

// TODO: More detailed error handling
@RestController
public class JwtController {

    private AuthenticationManager authenticationManager;

    private JwtTokenUtil jwtTokenUtil;

    private JwtUserDetailsService userDetailsService;

    @Autowired
    @Lazy
    JwtController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil,
            JwtUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtRequestDto authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponseDto(token));
    }

    @PostMapping("/users")
    public ResponseEntity<?> saveUser(@Valid @RequestBody RegisterUserDto userDto) throws Exception {
        userDetailsService.saveUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
