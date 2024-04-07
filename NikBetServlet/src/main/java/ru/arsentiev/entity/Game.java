package ru.arsentiev.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Game {
    private final int idGame;
    private Team homeTeam;
    private Team guestTeam;
    private Integer goalHomeTeam;
    private Integer goalGuestTeam;
    private LocalDateTime gameDate;
    private GameStatus status;
    private BigDecimal coefficientOnHomeTeam;
    private BigDecimal coefficientOnGuestTeam;
    private GameResult result;
}
