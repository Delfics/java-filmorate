package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.Friendship;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {
    Long id;
    @Email
    @NotNull
    String email;
    @NotNull
    @NotBlank
    String login;
    String name;
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate birthday;
    Set<Long> friends = new HashSet<>();
    Friendship friendship;
}
