// src/test/java/ru/kpfu/itis/group400/stashkov/dto/CreateUserDtoTest.java
package ru.kpfu.itis.group400.stashkov.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CreateUserDtoTest {

    @Test
    void shouldCreateDtoAndReturnFields() {
        CreateUserDto dto = new CreateUserDto("john", "secret", "john@example.com");
        assertThat(dto.username()).isEqualTo("john");
        assertThat(dto.password()).isEqualTo("secret");
        assertThat(dto.email()).isEqualTo("john@example.com");
    }
}