package ru.kpfu.itis.group400.stashkov.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.kpfu.itis.group400.stashkov.model.User;
import ru.kpfu.itis.group400.stashkov.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_shouldReturnCustomUserDetailsWhenUserExists() {
        // given
        User user = new User();
        user.setUsername("john");
        given(userRepository.findByUsernameWithRoles("john")).willReturn(Optional.of(user));

        // when
        UserDetails details = userDetailsService.loadUserByUsername("john");

        // then
        assertThat(details).isInstanceOf(CustomUserDetails.class);
        assertThat(details.getUsername()).isEqualTo("john");
    }

    @Test
    void loadUserByUsername_shouldThrowExceptionWhenUserNotFound() {
        // given
        given(userRepository.findByUsernameWithRoles("unknown")).willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("unknown");
    }
}