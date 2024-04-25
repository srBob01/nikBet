package ru.arsentiev.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Game {
    private long idGame;
    private Team homeTeam;
    private Team guestTeam;
    private Integer goalHomeTeam;
    private Integer goalGuestTeam;
    private LocalDateTime gameDate;
    private GameStatus status;
    private float coefficientOnHomeTeam;
    private float coefficientOnDraw;
    private float coefficientOnGuestTeam;
    private GameTime time;
    private GameResult result;
}
