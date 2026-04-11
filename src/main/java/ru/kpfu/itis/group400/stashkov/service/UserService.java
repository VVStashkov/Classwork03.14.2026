package ru.kpfu.itis.group400.stashkov.service;

import ru.kpfu.itis.group400.stashkov.dto.CreateUserDto;
import ru.kpfu.itis.group400.stashkov.dto.UserDto;
import ru.kpfu.itis.group400.stashkov.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto findByUsername(String username);

    void deleteUser(User user);

    void createUser(CreateUserDto user);

    void updateUser(User user);

    boolean verifyUser(String code);

}
