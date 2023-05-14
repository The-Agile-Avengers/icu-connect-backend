package com.agileavengers.icuconnectbackend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDto {

    /**
     * username to be registered
     */
    @NotNull(message = "Invalid name: Name is NULL")
    private String username;

    /**
     * Password to be set
     */
    @NotNull(message = "Invalid password: Password is NULL")
    private String password;

    /**
     * Email of the new user
     */
    @Email(message = "Invalid e-mail")
    @NotNull(message = "Invalid e-mail: E-mail is NULL")
    private String email;

}
