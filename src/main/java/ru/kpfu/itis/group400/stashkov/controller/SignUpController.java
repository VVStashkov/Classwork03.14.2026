package ru.kpfu.itis.group400.stashkov.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kpfu.itis.group400.stashkov.dto.CreateUserDto;
import ru.kpfu.itis.group400.stashkov.model.Role;
import ru.kpfu.itis.group400.stashkov.model.User;
import ru.kpfu.itis.group400.stashkov.repository.RoleRepository;
import ru.kpfu.itis.group400.stashkov.repository.UserRepository;
import ru.kpfu.itis.group400.stashkov.service.UserService;

import java.util.List;

@AllArgsConstructor
@Controller
public class SignUpController {

    private final UserService userService;

    @GetMapping("/signUp")
    public String showSignUpForm(Model model) {
        model.addAttribute("createUserDto", new CreateUserDto("", "", ""));
        return "signUp";
    }

    @PostMapping("/signUp")
    public String signUp(@ModelAttribute("createUserDto") CreateUserDto createUserDto,
                         RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(createUserDto);
            redirectAttributes.addFlashAttribute("message", "Registration successful! Please check your email for verification.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signUp";
        }
    }
}