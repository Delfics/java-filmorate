package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film /*implements Comparable<Film>*/{
    Long id;
    @NotNull
    String name;
    String description;
    LocalDate releaseDate;
    @NotNull
    Integer duration;
    Set<Long> likes = new HashSet<>();
    /*Optional<Integer> likesSize = Optional.ofNullable(likes.size()).orElse(0).describeConstable();*/

    /*@Override
    public int compareTo(Film o) {
        if (likesSize.isPresent() && o.getLikesSize().isPresent()) {
            return likesSize.get().compareTo(o.likesSize.get());
        } else {
            throw new RuntimeException("Невозможно сравнить если количество лайков равно null");
        }
    }*/
}
