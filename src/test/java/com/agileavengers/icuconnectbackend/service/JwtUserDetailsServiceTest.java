package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.mapper.UserMapper;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.JwtRequestDto;
import com.agileavengers.icuconnectbackend.model.dto.RegisterUserDto;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.gen5.api.Assertions.assertEquals;
import static org.junit.gen5.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest()
@ExtendWith({ SpringExtension.class, MockitoExtension.class })
//@ComponentScan(basePackageClasses = IcuConnectBackendApplication.class)
public class JwtUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private UserMapper userMapper;

    private User user;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        jwtUserDetailsService = new JwtUserDetailsService(userRepository, userMapper);

        user = User.builder()
                .id(1L)
                .username("TestUsername")
                .email("test@testmail.com")
                .password("password")
                .build();
    }

    @Test
    void loadUserByUsername() {
        JwtRequestDto jwtRequestDto = new JwtRequestDto("TestUsername", "password");

        doReturn(Optional.of(user)).when(userRepository).findByUsername(jwtRequestDto.getUsername());

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(jwtRequestDto.getUsername());

        assertEquals(userDetails.getUsername(), user.getUsername());
        assertEquals(userDetails.getPassword(), user.getPassword());
    }

    @Test
    void loadUserByUsernameThrowsException() {
        JwtRequestDto jwtRequestDto = new JwtRequestDto("TestUsername", "password");

        doReturn(Optional.empty()).when(userRepository).findByUsername(jwtRequestDto.getUsername());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            jwtUserDetailsService.loadUserByUsername(jwtRequestDto.getUsername());
        }, "UsernameNotFoundException was expected to be thrown.");

        assertEquals("User not found with username: " + user.getUsername(),
                exception.getMessage());
    }

    @Test
    void createUser() {
        this.jwtUserDetailsService = new JwtUserDetailsService(userRepository, userMapper);
        RegisterUserDto registerUserDto = RegisterUserDto.builder()
                .username("TestUsername")
                .password("password")
                .email("test@testmail.com").build();

        doReturn(Optional.empty()).when(userRepository).findByUsername(anyString());

        when(userRepository.save(any())).thenReturn(user);

        User savedUser = jwtUserDetailsService.saveUser(registerUserDto);

        assertNotNull(savedUser);
        assertEquals(user.getId(), savedUser.getId());
        assertEquals(user.getUsername(), savedUser.getUsername());
        assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    void createUserWhenUsernameAlreadyExistsThrowsException() {
        this.jwtUserDetailsService = new JwtUserDetailsService(userRepository, userMapper);
        RegisterUserDto registerUserDto = RegisterUserDto.builder().username("TestUsername").password("password")
                .email("anotherTest@testmail.com").build();

        doReturn(Optional.of(user)).when(userRepository).findByUsername(anyString());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jwtUserDetailsService.saveUser(registerUserDto);
        }, "ResponeStatusException");

        assertEquals("Username already taken.", exception.getReason());
    }

    @Test
    void createUserWhenEmailAlreadyExistsThrowsException() {
        this.jwtUserDetailsService = new JwtUserDetailsService(userRepository, userMapper);
        RegisterUserDto registerUserDto = RegisterUserDto.builder().username("AnotherTestUsername").password("password")
                .email("test@testmail.com").build();

        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jwtUserDetailsService.saveUser(registerUserDto);
        }, "ResponeStatusException");

        assertEquals("Email already taken.", exception.getReason());
    }
}
