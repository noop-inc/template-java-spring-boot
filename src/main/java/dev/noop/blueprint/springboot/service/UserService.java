package dev.noop.blueprint.springboot.service;


import dev.noop.blueprint.springboot.dto.UserDto;
import dev.noop.blueprint.springboot.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();
}