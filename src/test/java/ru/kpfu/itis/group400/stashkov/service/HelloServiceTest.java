package ru.kpfu.itis.group400.stashkov.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloServiceTest {

    private final HelloService helloService = new HelloService();

    @ParameterizedTest
    @ValueSource(strings = {"Vlad", "Ivan"})
    public void sayHelloTest(String name) {
        assertEquals("Hello, " + name,helloService.sayHello(name));
    }



}
