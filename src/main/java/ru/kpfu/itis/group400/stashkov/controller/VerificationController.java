package ru.kpfu.itis.group400.stashkov.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kpfu.itis.group400.stashkov.aop.BenchMark;
import ru.kpfu.itis.group400.stashkov.service.UserService;

@AllArgsConstructor
@Controller
public class VerificationController {

    private final UserService userService;

    @BenchMark
    @GetMapping("/verification")
    public String verify(@RequestParam("code") String code,
                         RedirectAttributes redirectAttributes) {
        boolean verified = userService.verifyUser(code);
        if (verified) {
            redirectAttributes.addFlashAttribute("message", "Your account has been verified. You can now log in.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired verification link.");
        }
        return "redirect:/login";
    }

}
