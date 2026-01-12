package fr.dylanhanique.collectoryapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest (
        @NotBlank @Email String email,
        @Size(min = 8) String password
) {}
