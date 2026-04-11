package ru.kpfu.itis.group400.stashkov.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.group400.stashkov.config.properties.MailProperties;
import ru.kpfu.itis.group400.stashkov.dto.CreateUserDto;
import ru.kpfu.itis.group400.stashkov.dto.UserDto;
import ru.kpfu.itis.group400.stashkov.model.Role;
import ru.kpfu.itis.group400.stashkov.model.User;
import ru.kpfu.itis.group400.stashkov.repository.RoleRepository;
import ru.kpfu.itis.group400.stashkov.repository.UserRepository;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

//    private final UserRepositoryHiber userRepositoryHiber;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailProperties mailProperties;
    private final JavaMailSender mailSender;

    @Override
    public List<UserDto> getAll() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

//        return userRepositoryHiber.getAll().stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
    }

    @Override
    public UserDto findByUsername(String username){
        User user = userRepository.findByUsername(username).orElse(null);
        return convertToDto(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public void createUser(CreateUserDto createUserDto) {

        if (userRepository.findByUsername(createUserDto.username()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.findByEmail(createUserDto.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setUsername(createUserDto.username());
        user.setPassword(passwordEncoder.encode(createUserDto.password()));
        user.setEmail(createUserDto.email());
        user.setEnabled(false);
        String verificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(verificationCode);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    return roleRepository.save(role);
                });
        user.setRoles(List.of(userRole));

        userRepository.save(user);

        sendVerificationMail(createUserDto, verificationCode);
    }

    @Override
    public boolean verifyUser(String code) {
        Optional<User> userOpt = userRepository.findByVerificationCode(code);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(true);
            user.setVerificationCode(null);  // clear the code after verification
            userRepository.save(user);
            return true;
        }
        return false;
    }


    @Override
    public void updateUser(User user) {
        userRepository.update(user);
    }

    private UserDto convertToDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getUsername());
    }

    private void sendVerificationMail(CreateUserDto createUserDto, String verificationCode) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        String content = mailProperties.content();
        try {
            mimeMessageHelper.setFrom(mailProperties.from(), mailProperties.sender());
            mimeMessageHelper.setTo(createUserDto.email());
            mimeMessageHelper.setSubject(mailProperties.subject());

            content = content.replace("$name", createUserDto.username());
            content = content.replace("$url", mailProperties.baseUrl() +
                    "/verification?code=" + verificationCode);

            mimeMessageHelper.setText(content, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}