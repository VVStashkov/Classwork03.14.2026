package ru.kpfu.itis.group400.stashkov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.kpfu.itis.group400.stashkov.dto.CreateUserDto;
import ru.kpfu.itis.group400.stashkov.service.UserServiceImpl;

@Controller
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }


    @PostMapping("/users")
    public String createUser(@RequestBody CreateUserDto createUserDto) {
        userService.createUser(createUserDto);

        return "success_sign_up";
    }

}