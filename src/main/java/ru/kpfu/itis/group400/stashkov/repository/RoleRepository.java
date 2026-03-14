package ru.kpfu.itis.group400.stashkov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.itis.group400.stashkov.model.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}