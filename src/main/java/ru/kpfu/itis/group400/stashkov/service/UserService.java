package ru.kpfu.itis.group400.stashkov.service;

import org.springframework.stereotype.Service;
import ru.kpfu.itis.group400.stashkov.dto.UserDto;
import ru.kpfu.itis.group400.stashkov.model.User;
import ru.kpfu.itis.group400.stashkov.repository.UserRepository;
import ru.kpfu.itis.group400.stashkov.repository.UserRepositoryHiber;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepositoryHiber userRepositoryHiber;
    private final UserRepository userRepository;

    public UserService(UserRepositoryHiber userRepositoryHiber, UserRepository userRepository) {
        this.userRepositoryHiber = userRepositoryHiber;
        this.userRepository = userRepository;
    }

    public List<UserDto> findAll() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

//        return userRepositoryHiber.findAll().stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
    }

    public UserDto findByUsername(String username){
        User user = userRepository.findByUsername(username).orElse(null);
        return convertToDto(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void createUser(User user) {
        userRepository.create(user);
    }

    public void updateUser(User user) {
        userRepository.update(user);
    }

    private UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getUsername());
    }
}