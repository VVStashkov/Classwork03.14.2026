package ru.kpfu.itis.group400.stashkov.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kpfu.itis.group400.stashkov.config.properties.MailProperties;
import ru.kpfu.itis.group400.stashkov.dto.CreateUserDto;
import ru.kpfu.itis.group400.stashkov.dto.UserDto;
import ru.kpfu.itis.group400.stashkov.model.Role;
import ru.kpfu.itis.group400.stashkov.model.User;
import ru.kpfu.itis.group400.stashkov.repository.RoleRepository;
import ru.kpfu.itis.group400.stashkov.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailProperties mailProperties;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAll_shouldReturnListOfUserDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        given(userRepository.findAll()).willReturn(List.of(user));

        List<UserDto> result = userService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getUsername()).isEqualTo("john");
    }

    @Test
    void findByUsername_whenUserExists_shouldReturnUserDto() {
        User user = new User();
        user.setId(2L);
        user.setUsername("jane");
        given(userRepository.findByUsername("jane")).willReturn(Optional.of(user));

        UserDto result = userService.findByUsername("jane");

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getUsername()).isEqualTo("jane");
    }

    @Test
    void findByUsername_whenUserNotFound_shouldReturnNull() {
        given(userRepository.findByUsername("missing")).willReturn(Optional.empty());

        UserDto result = userService.findByUsername("missing");

        assertThat(result).isNull();
    }

    @Test
    void deleteUser_shouldCallRepositoryDelete() {
        User user = new User();
        userService.deleteUser(user);
        verify(userRepository).delete(user);
    }

    @Test
    void createUser_shouldSaveUserWithEncodedPasswordAndVerificationCode() {
        CreateUserDto dto = new CreateUserDto("newuser", "pass", "mail@ex.com");
        given(userRepository.findByUsername("newuser")).willReturn(Optional.empty());
        given(userRepository.findByEmail("mail@ex.com")).willReturn(Optional.empty());
        given(passwordEncoder.encode("pass")).willReturn("encodedPass");
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.of(userRole));
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);
        given(mailProperties.content()).willReturn("Hello $name, click $url");
        given(mailProperties.from()).willReturn("noreply@test.com");
        given(mailProperties.sender()).willReturn("Test App");
        given(mailProperties.subject()).willReturn("Verify");
        given(mailProperties.baseUrl()).willReturn("http://localhost:8080");

        userService.createUser(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getUsername()).isEqualTo("newuser");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPass");
        assertThat(savedUser.getEmail()).isEqualTo("mail@ex.com");
        assertThat(savedUser.isEnabled()).isFalse();
        assertThat(savedUser.getVerificationCode()).isNotNull();
        assertThat(savedUser.getRoles()).hasSize(1);
        assertThat(savedUser.getRoles().get(0).getName()).isEqualTo("ROLE_USER");

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void createUser_whenUsernameTaken_shouldThrowException() {
        CreateUserDto dto = new CreateUserDto("existing", "pass", "mail@ex.com");
        given(userRepository.findByUsername("existing")).willReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already taken");

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_whenEmailTaken_shouldThrowException() {
        CreateUserDto dto = new CreateUserDto("new", "pass", "used@ex.com");
        given(userRepository.findByUsername("new")).willReturn(Optional.empty());
        given(userRepository.findByEmail("used@ex.com")).willReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void verifyUser_withValidCode_shouldEnableUser() {
        User user = new User();
        user.setEnabled(false);
        user.setVerificationCode("code123");
        given(userRepository.findByVerificationCode("code123")).willReturn(Optional.of(user));

        boolean result = userService.verifyUser("code123");

        assertThat(result).isTrue();
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getVerificationCode()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    void verifyUser_withInvalidCode_shouldReturnFalse() {
        given(userRepository.findByVerificationCode("invalid")).willReturn(Optional.empty());

        boolean result = userService.verifyUser("invalid");

        assertThat(result).isFalse();
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldCallRepositoryUpdate() {
        User user = new User();
        userService.updateUser(user);
        verify(userRepository).update(user);
    }


    // Добавить в существующий UserServiceImplTest

    @Test
    void createUser_whenRoleNotFound_shouldCreateNewRole() {
        CreateUserDto dto = new CreateUserDto("newuser", "pass", "mail@ex.com");
        given(userRepository.findByUsername("newuser")).willReturn(Optional.empty());
        given(userRepository.findByEmail("mail@ex.com")).willReturn(Optional.empty());
        given(passwordEncoder.encode("pass")).willReturn("encoded");
        given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.empty());
        Role newRole = new Role();
        newRole.setName("ROLE_USER");
        given(roleRepository.save(any(Role.class))).willReturn(newRole);
        given(mailSender.createMimeMessage()).willReturn(mock(MimeMessage.class));
        given(mailProperties.content()).willReturn("...");
        given(mailProperties.from()).willReturn("...");
        given(mailProperties.baseUrl()).willReturn("...");
        given(mailProperties.sender()).willReturn("...");
        given(mailProperties.subject()).willReturn("...");

        userService.createUser(dto);

        verify(roleRepository).save(any(Role.class));
        verify(userRepository).save(any(User.class));
    }

}