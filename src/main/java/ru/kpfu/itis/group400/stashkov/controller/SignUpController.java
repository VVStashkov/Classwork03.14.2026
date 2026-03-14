package ru.kpfu.itis.group400.stashkov.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kpfu.itis.group400.stashkov.model.Role;
import ru.kpfu.itis.group400.stashkov.model.User;
import ru.kpfu.itis.group400.stashkov.repository.RoleRepository;
import ru.kpfu.itis.group400.stashkov.repository.UserRepository;

import java.util.List;

@Controller
public class SignUpController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public SignUpController(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/signUp")
    public String showSignUpForm() {
        return "signUp";
    }

    @PostMapping("/signUp")
    public String signUp(@RequestParam("name") String name,
                         @RequestParam("password") String password) {
        if (userRepository.findByUsername(name).isPresent()) {
            return "redirect:/signUp?error=exists";
        }

        User user = new User();
        user.setUsername(name);
        user.setPassword(passwordEncoder.encode(password));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    return roleRepository.save(role);
                });
        user.setRoles(List.of(userRole));

        userRepository.save(user);
        return "redirect:/login?registered";
    }
}