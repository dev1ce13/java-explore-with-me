package ru.yandex.practicum.main.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.main.user.dto.NewUserRequestDto;
import ru.yandex.practicum.main.user.dto.UserDto;
import ru.yandex.practicum.main.user.exception.DuplicateEmailException;
import ru.yandex.practicum.main.user.mapper.UserMapper;
import ru.yandex.practicum.main.user.model.User;
import ru.yandex.practicum.main.user.repository.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> adminGetUsers(List<Integer> ids, int from, int size) {
        List<User> users = userRepository.findAllById(ids);
        checkingFromParameter(from, users.size());
        return users.subList(from, users.size())
                .stream()
                .limit(size)
                .map(UserMapper::mapToUserDtoFromUser)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto adminCreateUser(NewUserRequestDto newUserRequestDto) {
        if (userRepository.findByEmail(newUserRequestDto.getEmail()) != null) {
            throw new DuplicateEmailException("could not execute statement;",
                    new SQLException("nested exception is org.hibernate.exception. " +
                            "ConstraintViolationException: could not execute statement"),
                    "SQL [n/a];",
                    "constraint [uq_user_email];");
        }
        User user = UserMapper.mapToUserFromNewUserRequestDto(newUserRequestDto);
        return UserMapper.mapToUserDtoFromUser(userRepository.save(user));
    }

    @Override
    @Transactional
    public void adminDeleteUser(int id) {
        userRepository.deleteById(id);
    }

    private void checkingFromParameter(int from, int listSize) {
        if (from > listSize) {
            throw new IllegalArgumentException("Parameter from must be lower size list");
        }
    }
}
