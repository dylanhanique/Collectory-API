package fr.dylanhanique.collectoryapi.mapper;

import fr.dylanhanique.collectoryapi.dto.UserResponse;
import fr.dylanhanique.collectoryapi.model.User;

public class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

}
