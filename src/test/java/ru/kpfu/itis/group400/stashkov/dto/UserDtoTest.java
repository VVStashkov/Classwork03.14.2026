package ru.kpfu.itis.group400.stashkov.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTest {

    @Test
    void shouldCreateDtoAndReturnFields() {
        UserDto dto = new UserDto(42L, "alice");
        assertThat(dto.getId()).isEqualTo(42L);
        assertThat(dto.getUsername()).isEqualTo("alice");
    }
}