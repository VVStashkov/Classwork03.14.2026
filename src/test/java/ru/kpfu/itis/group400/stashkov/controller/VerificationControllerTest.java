package ru.kpfu.itis.group400.stashkov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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

@WebMvcTest(VerificationController.class)
public class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    public void testVerifySuccessful() throws Exception {
        given(userService.verifyUser(any(String.class))).willReturn(true);
        String uuid = "uuid";
        mockMvc.perform(get("/verification")
                        .with(user("user").roles("USER"))
                        .with(csrf())
                        .param("code", uuid))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    public void testVerifyFailed() throws Exception {
        given(userService.verifyUser(any(String.class))).willReturn(false);
        String uuid = "uuid";
        mockMvc.perform(get("/verification")
                        .with(user("user").roles("USER"))
                        .with(csrf())
                        .param("code", uuid))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("error"));
    }

}
