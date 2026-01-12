package fr.dylanhanique.collectoryapi.dto;

public record LoginResponse (
        String token,
        Long expiresIn
) {}
