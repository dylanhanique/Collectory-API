package fr.dylanhanique.collectoryapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest (
    @NotBlank String username,
    @Email String email,
    @Size(min = 8) String password
) {}
