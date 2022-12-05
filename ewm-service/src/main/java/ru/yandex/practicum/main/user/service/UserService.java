package ru.yandex.practicum.main.user.service;

import ru.yandex.practicum.main.user.dto.NewUserRequestDto;
import ru.yandex.practicum.main.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> adminGetUsers(List<Integer> ids, int from, int size);

    UserDto adminCreateUser(NewUserRequestDto newUserRequestDto);

    void adminDeleteUser(int id);
}
