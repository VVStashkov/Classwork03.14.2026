package ru.kpfu.itis.group400.stashkov.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kpfu.itis.group400.stashkov.model.Role;
import ru.kpfu.itis.group400.stashkov.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsTest {

    @Test
    void shouldReturnAuthoritiesFromUserRoles() {
        User user = new User();
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRoles(List.of(role));
        user.setPassword("encodedPass");
        user.setUsername("john");
        user.setEnabled(true);

        CustomUserDetails details = new CustomUserDetails(user);

        assertThat(details.getAuthorities())
                .hasSize(1)
                .first()
                .satisfies(auth -> {
                    assertThat(auth.getAuthority()).isEqualTo("ROLE_USER");
                });
        assertThat(details.getPassword()).isEqualTo("encodedPass");
        assertThat(details.getUsername()).isEqualTo("john");
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getUser()).isSameAs(user);
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenUserHasNoRoles() {
        User user = new User();
        user.setRoles(null);
        CustomUserDetails details = new CustomUserDetails(user);
        assertThat(details.getAuthorities()).isEmpty();
    }
}