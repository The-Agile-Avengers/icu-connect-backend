package com.agileavengers.icuconnectbackend.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.gen5.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.agileavengers.icuconnectbackend.mapper.UserMapper;
import com.agileavengers.icuconnectbackend.mapper.UserMapperImpl;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.RegisterUserDto;
import com.agileavengers.icuconnectbackend.repository.UserRepository;

@SpringBootTest(classes = { UserMapperImpl.class })
public class JwtUserDetailsServiceTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder bcryptEncoder;

    @MockBean
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private UserMapper userMapper;

    private User user;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        jwtUserDetailsService = new JwtUserDetailsService(userRepository, userMapper);

        user = User.builder()
                .id(1L)
                .username("TestUsername")
                .email("test@testmail.com")
                .build();
    }

    @Test
    void testLoadUserByUsername() {

    }

    // @Test
    void testSaveUser() {
        RegisterUserDto registerUserDto = RegisterUserDto.builder().username("TestUsername").password("password")
                .email("test@testmail.com").build();

        doReturn(Optional.empty()).when(userRepository).findByUsername(anyString());

        when(userRepository.save(any())).thenReturn(user);

        User savedUser = jwtUserDetailsService.saveUser(registerUserDto);

        assertThat(savedUser).isNotNull();
        assertEquals(user.getId(), savedUser.getId());
        assertEquals(user.getUsername(), savedUser.getUsername());
        assertEquals(user.getEmail(), savedUser.getEmail());

    }
}
