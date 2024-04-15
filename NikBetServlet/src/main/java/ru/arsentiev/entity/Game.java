package ru.arsentiev.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Game {
    private Long idGame;
    private Team homeTeam;
    private Team guestTeam;
    private Integer goalHomeTeam;
    private Integer goalGuestTeam;
    private LocalDateTime gameDate;
    private GameStatus status;
    private Float coefficientOnHomeTeam;
    private Float coefficientOnDraw;
    private Float coefficientOnGuestTeam;
    private GameTime time;
    private GameResult result;
}
