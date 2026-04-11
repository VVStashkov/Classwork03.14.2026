package ru.kpfu.itis.group400.stashkov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kpfu.itis.group400.stashkov.dto.UserDto;
import ru.kpfu.itis.group400.stashkov.service.HelloService;
import ru.kpfu.itis.group400.stashkov.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.BDDMockito.given;

@WebMvcTest(HelloController.class)
public class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HelloService helloService;
    @MockitoBean
    private UserService userService;

    @Test
    public void helloTest() throws Exception {
        String name = "Ivan";
        String expected = "Hello, Ivan";
        given(helloService.sayHello(name)).willReturn(expected);

        mockMvc.perform(get("/hello").param("name", name)
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    @Test
    public void findAllTest() throws Exception {
        UserDto userDto1 = new UserDto(1L, "Ivan");
        UserDto userDto2 = new UserDto(2L, "Vlad");
        given(userService.getAll()).willReturn(List.of(userDto1, userDto2));

        mockMvc.perform(get("/users").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("Ivan"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("Vlad"));

    }

}
