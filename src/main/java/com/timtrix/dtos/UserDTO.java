package com.timtrix.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {

    @NotEmpty(message = "First name is required")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    private String lastName;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @Size(min = 6, message = "Password should be at least 6 characters")
    @NotEmpty(message = "Password is required")
    private String password;

    @Pattern(regexp = "^\\d{11}$", message = "Phone number must be exactly 11 digits")
    private String phone;

}

