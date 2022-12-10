package ru.yandex.practicum.main.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.main.user.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);
}
