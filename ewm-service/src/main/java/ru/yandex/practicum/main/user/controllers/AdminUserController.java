package ru.yandex.practicum.main.user.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.main.user.dto.NewUserRequestDto;
import ru.yandex.practicum.main.user.dto.UserDto;
import ru.yandex.practicum.main.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> adminGetUsers(
            @RequestParam(name = "ids") List<Integer> ids,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("/GET admin users admin");
        return service.adminGetUsers(ids, from, size);
    }

    @PostMapping
    public UserDto adminCreateUser(
            @Valid @RequestBody NewUserRequestDto newUserRequestDto
    ) {
        log.info("/POST admin user");
        return service.adminCreateUser(newUserRequestDto);
    }

    @DeleteMapping("/{userId}")
    public void adminDeleteUser(
            @PathVariable(name = "userId") int id
    ) {
        log.info("/DELETE admin user with id={}", id);
        service.adminDeleteUser(id);
    }
}
