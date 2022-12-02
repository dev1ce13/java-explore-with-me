package ru.yandex.practicum.main.user.mapper;

import ru.yandex.practicum.main.user.dto.NewUserRequestDto;
import ru.yandex.practicum.main.user.dto.UserDto;
import ru.yandex.practicum.main.user.dto.UserShortDto;
import ru.yandex.practicum.main.user.model.User;

public class UserMapper {

    public static User mapToUserFromNewUserRequestDto(NewUserRequestDto newUserRequestDto) {
        return new User(0, newUserRequestDto.getName(), newUserRequestDto.getEmail());
    }

    public static UserShortDto mapToUserShortDtoFromUser(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static UserDto mapToUserDtoFromUser(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
