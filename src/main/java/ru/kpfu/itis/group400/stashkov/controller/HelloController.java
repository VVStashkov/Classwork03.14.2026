package ru.kpfu.itis.group400.stashkov.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kpfu.itis.group400.stashkov.dto.UserDto;
import ru.kpfu.itis.group400.stashkov.service.HelloService;
import ru.kpfu.itis.group400.stashkov.service.UserServiceImpl;

import java.util.List;

@RestController
public class HelloController {

    private final HelloService helloService;
    private final UserServiceImpl userService;

    public HelloController(HelloService helloService, UserServiceImpl userService) {
        this.helloService = helloService;
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(required = false, name = "name") String name) {
        return helloService.sayHello(name);
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> findAll() {   // возвращаем DTO, а не сущности
        return userService.findAll();
    }
}