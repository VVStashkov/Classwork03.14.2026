package ru.kpfu.itis.group400.stashkov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kpfu.itis.group400.stashkov.dto.CreateUserDto;
import ru.kpfu.itis.group400.stashkov.service.UserService;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SignUpController.class)
public class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;


    @Test
    public void showSignUpForm() throws Exception {
        mockMvc.perform(get("/signUp")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("signUp"));
    }

    @Test
    public void singUpWithIllegalArgs_shouldRedirectToSignUpWithError() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto(
                "username", "password", "email");

        doThrow(new IllegalArgumentException("Username already exists."))
                .when(userService).createUser(any(CreateUserDto.class));

        mockMvc.perform(post("/signUp")
                        .with(user("user").roles("USER"))
                        .with(csrf())
                        .param("username", createUserDto.username())
                        .param("password", createUserDto.password())
                        .param("email", createUserDto.email()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signUp"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "Username already exists."));

    }

    @Test
    public void signUpWithValidData_shouldRedirectToLogin() throws Exception {

        CreateUserDto createUserDto = new CreateUserDto(
                "username", "password", "email");

        mockMvc.perform(post("/signUp")
                        .with(csrf())
                        .with(user("user").roles("USER"))
                        .param("username", createUserDto.username())
                        .param("password", createUserDto.password())
                        .param("email", createUserDto.email()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

}
