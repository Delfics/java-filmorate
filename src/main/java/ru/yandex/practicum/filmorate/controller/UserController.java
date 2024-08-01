package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.mappers.MapUserDtoToUser;
import ru.yandex.practicum.filmorate.dto.mappers.MapUserToUserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<UserDto> getAllValues() {
        Collection<User> allValues = userService.getAllValues();
        return allValues.stream()
                .map(MapUserToUserDto::userToUserDto)
                .toList();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        User byId = userService.getById(id);
        return MapUserToUserDto.userToUserDto(byId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = MapUserDtoToUser.userDtoToUser(userDto);
        User createUser = userService.create(user);
        return MapUserToUserDto.userToUserDto(createUser);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UserDto newUserDto) {
        User user = MapUserDtoToUser.userDtoToUser(newUserDto);
        User update = userService.update(user);
        return MapUserToUserDto.userToUserDto(update);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public UserDto addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        User user = userService.addFriend(userId, friendId);
        return MapUserToUserDto.userToUserDto(user);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.deleteFriendById(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<UserDto> getAllFriends(@PathVariable Long userId) {
        Set<User> allFriends = userService.getAllFriends(userId);
        return allFriends.stream()
                .map(MapUserToUserDto::userToUserDto)
                .toList();
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Set<UserDto> getCollectiveFriends(@PathVariable Long userId, @PathVariable Long otherId) {
        Set<User> collectiveFriends = userService.getCollectiveFriends(userId, otherId);
        return collectiveFriends.stream()
                .map(MapUserToUserDto::userToUserDto)
                .collect(Collectors.toSet());
    }

    @PostMapping("/{userId}/friends/{friendId}/confirm")
    public void confirmFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.confirmFriend(userId, friendId);
    }

}
