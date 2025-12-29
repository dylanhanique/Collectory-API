package fr.dylanhanique.collectoryapi.dto;

public record UserResponse (
    Long id,
    String username,
    String email
) {}
