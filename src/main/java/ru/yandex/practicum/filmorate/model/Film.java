package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.Genre;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    Long id;
    @NotNull
    String name;
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;
    @NotNull
    Integer duration;
    Set<Like> likes = new HashSet<>();
    Set<Genre> genres = new HashSet<>();
    Mpa mpa;




/*    private HashMap<String, String> MpaResources() {
        HashMap <String, String> map = new HashMap<>();
        map.put("G", " — у фильма нет возрастных ограничений.");
        map.put("PG", " — детям рекомендуется смотреть фильм с родителями.");
        map.put("PG-13", " — детям до 13 лет просмотр не желателен.");
        map.put("R", " лицам до 17 лет просматривать фильм можно только в присутствии взрослого.");
        map.put("NC-17", " — лицам до 18 лет просмотр запрещён.");
        return map;
    }*/

}
